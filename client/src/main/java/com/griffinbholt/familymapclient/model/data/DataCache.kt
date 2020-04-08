package com.griffinbholt.familymapclient.model.data

import com.griffinbholt.familymapclient.controller.utils.IconGenerator
import com.griffinbholt.familymapclient.model.Settings
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import com.griffinbholt.familymapclient.model.data.item.SideOfFamily
import com.griffinbholt.familymapclient.model.data.utils.SearchTool
import shared.model.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A singleton object that stores user data.
 */
object DataCache {

	/**
	 * The personID associated with the user
	 */
	var personID: String? = null

	/**
	 * The [authorization token][AuthToken] received from the FamilyMap server after the user logs in.
	 */
	var authToken: AuthToken? = null

	/**
	 * The [ClientPerson] object representing the user
	 */
	var user: ClientPerson? = null

	/**
	 * @return The user's first name
	 */
	fun firstName(): String? = user?.firstName

	/**
	 * @return The user's last name
	 */
	fun lastName(): String? = user?.lastName

	private var motherSideFemales: ArrayList<ClientPerson> = ArrayList()
	private var motherSideMales: ArrayList<ClientPerson> = ArrayList()
	private var fatherSideFemales: ArrayList<ClientPerson> = ArrayList()
	private var fatherSideMales: ArrayList<ClientPerson> = ArrayList()

	private val personCaches: List<List<ClientPerson>> =
			listOf(motherSideFemales, motherSideMales, fatherSideFemales, fatherSideMales)

	private var immediateFamilyMaleEvents: ArrayList<ClientEvent> = ArrayList()
	private var immediateFamilyFemaleEvents: ArrayList<ClientEvent> = ArrayList()
	private var motherSideFemaleEvents: ArrayList<ClientEvent> = ArrayList()
	private var motherSideMaleEvents: ArrayList<ClientEvent> = ArrayList()
	private var fatherSideFemaleEvents: ArrayList<ClientEvent> = ArrayList()
	private var fatherSideMaleEvents: ArrayList<ClientEvent> = ArrayList()

	private var familyMembersTmpCache: List<ServerPerson>? = null
	private var familyEventsTmpCache: List<ServerEvent>? = null

	/**
	 * Processes a list of [ServerPerson] objects, converts them to [ClientPerson] objects,
	 * and saves them in the [DataCache] for use during the life of the application.
	 *
	 * @param familyMembers A list of [ServerPerson] objects to load into the [DataCache]
	 */
	fun loadFamilyMembers(familyMembers: List<ServerPerson>) {
		familyMembersTmpCache = familyMembers

		val userPerson: ServerPerson = findServerPerson(personID)!!

		loadUserParents(userPerson)

		userPerson.spouseID?.let { loadUserSpouse(it) }
	}

	private fun loadUserParents(userPerson: ServerPerson) {
		val serverMother: ServerPerson? = findServerPerson(userPerson.motherID)
		val serverFather: ServerPerson? = findServerPerson(userPerson.fatherID)

		val clientMother: ClientPerson? = serverMother?.let { recursivelyLoadFamilyMembers(it, SideOfFamily.MOTHER) }
		val clientFather: ClientPerson? = serverFather?.let { recursivelyLoadFamilyMembers(it, SideOfFamily.FATHER) }

		connectSpouses(clientMother, clientFather)

		user = ClientPerson(SideOfFamily.IMMEDIATE, clientMother, clientFather, userPerson)

		connectChildToParents(clientFather, user!!, clientMother)
	}

	private fun loadUserSpouse(spouseID: String) {
		val userSpousePerson: ServerPerson? = findServerPerson(spouseID)

		if (userSpousePerson != null) {
			user!!.spouse = ClientPerson(SideOfFamily.IMMEDIATE, null, null, userSpousePerson)
		}
	}

	private fun findServerPerson(personID: String?): ServerPerson? {
		return familyMembersTmpCache!!.find { it.personID == personID }
	}

	private fun recursivelyLoadFamilyMembers(person: ServerPerson?, sideOfFamily: SideOfFamily): ClientPerson? {
		// Base Case #1 - The person does not exist
		if (person == null) {
			return null
		}

		// Recursive Case - The person does exist
		val serverMother: ServerPerson? = findServerPerson(person.motherID)
		val serverFather: ServerPerson? = findServerPerson(person.fatherID)

		val clientMother: ClientPerson? = serverMother?.let { recursivelyLoadFamilyMembers(it, sideOfFamily) }
		val clientFather: ClientPerson? = serverFather?.let { recursivelyLoadFamilyMembers(it, sideOfFamily) }

		connectSpouses(clientMother, clientFather)

		val clientPerson = ClientPerson(sideOfFamily, clientMother, clientFather, person)

		connectChildToParents(clientFather, clientPerson, clientMother)

		addToCorrectPersonCache(clientPerson, sideOfFamily)

		return clientPerson
	}

	private fun connectChildToParents(
			clientFather: ClientPerson?,
			clientPerson: ClientPerson,
			clientMother: ClientPerson?
	) {
		clientFather?.addChild(clientPerson)
		clientMother?.addChild(clientPerson)
	}

	private fun connectSpouses(clientMother: ClientPerson?, clientFather: ClientPerson?) {
		clientMother?.spouse = clientFather
		clientFather?.spouse = clientMother
	}

	private fun addToCorrectPersonCache(clientPerson: ClientPerson, sideOfFamily: SideOfFamily) {
		if (clientPerson.gender == Gender.FEMALE && sideOfFamily == SideOfFamily.MOTHER) {
			motherSideFemales.add(clientPerson)
		} else if (clientPerson.gender == Gender.MALE && sideOfFamily == SideOfFamily.MOTHER) {
			motherSideMales.add(clientPerson)
		} else if (clientPerson.gender == Gender.FEMALE) {
			fatherSideFemales.add(clientPerson)
		} else {
			fatherSideMales.add(clientPerson)
		}
	}

	/**
	 * Processes a list of [ServerEvent] objects, converts them to [ClientEvent] objects,
	 * and saves them in the [DataCache] for use during the life of the application.
	 *
	 * @param familyEvents A list of [ServerEvent] objects to load into the [DataCache]
	 */
	fun loadFamilyEvents(familyEvents: List<ServerEvent>) {
		val possibleEventTypes: MutableSet<EventType> = TreeSet()

		familyEventsTmpCache = familyEvents

		for (event in familyEventsTmpCache!!) {
			val person: ClientPerson = findClientPerson(event.personID)!!

			val clientEvent = ClientEvent(person, event)

			person.addEvent(clientEvent)

			addToCorrectEventCache(person, clientEvent)

			possibleEventTypes.add(clientEvent.eventType)
		}

		recordPossibleEventTypes(possibleEventTypes)

		clearTemporaryCaches()
	}

	private fun recordPossibleEventTypes(possibleEventTypes: Set<EventType>) {
		IconGenerator.setPossibleEventTypes(possibleEventTypes.toList())
	}

	private fun clearTemporaryCaches() {
		familyMembersTmpCache = null
		familyEventsTmpCache = null
	}

	@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
	private fun addToCorrectEventCache(person: ClientPerson, clientEvent: ClientEvent) {
		val gender: Gender = person.gender
		val sideOfFamily: SideOfFamily = person.sideOfFamily

		if (sideOfFamily == SideOfFamily.MOTHER && gender == Gender.FEMALE) {
			motherSideFemaleEvents.add(clientEvent)
		} else if (sideOfFamily == SideOfFamily.MOTHER && gender == Gender.MALE) {
			motherSideMaleEvents.add(clientEvent)
		} else if (sideOfFamily == SideOfFamily.FATHER && gender == Gender.FEMALE) {
			fatherSideFemaleEvents.add(clientEvent)
		} else if (sideOfFamily == SideOfFamily.FATHER && gender == Gender.MALE) {
			fatherSideMaleEvents.add(clientEvent)
		} else if (gender == Gender.FEMALE) {
			immediateFamilyFemaleEvents.add(clientEvent)
		} else {
			immediateFamilyMaleEvents.add(clientEvent)
		}
	}

	private fun findClientPerson(personID: String): ClientPerson? {
		var clientPerson: ClientPerson? = checkUser(personID)

		if (clientPerson == null) {
			clientPerson = checkUserSpouse(personID)
		}

		if (clientPerson == null) {
			clientPerson = checkPersonCaches(personID, clientPerson)
		}

		return clientPerson
	}

	private fun checkPersonCaches(personID: String, clientPerson: ClientPerson?): ClientPerson? {
		var foundClientPerson: ClientPerson? = clientPerson

		for (cache in personCaches) {
			if (foundClientPerson == null) {
				foundClientPerson = checkPersonCache(personID, cache)
			} else {
				return foundClientPerson
			}
		}

		return foundClientPerson
	}

	private fun checkUser(personID: String): ClientPerson? {
		return if (DataCache.personID == personID) user else null
	}

	private fun checkUserSpouse(personID: String): ClientPerson? {
		return if (user!!.spouse?.personID == personID) user!!.spouse else null
	}

	private fun checkPersonCache(personID: String, cache: List<ClientPerson>): ClientPerson? {
		for (person in cache) {
			if (person.personID == personID) {
				return person
			}
		}

		return null
	}

	/**
	 * Sets the text query against which [ClientPerson] and [ClientEvent] objects in the [DataCache]
	 * will be checked using the [SearchTool]
	 *
	 * @param textQuery A [String] for which matching [ClientPerson] and [ClientEvent] objects will be sought
	 */
	fun setTextQuery(textQuery: String) {
		SearchTool.setTextQuery(textQuery)
	}

	/**
	 * Searches all of the [ClientPerson] objects in the [DataCache] that are enabled by the current filters
	 * in the [com.griffinbholt.familymapclient.model.Settings] singleton using the [SearchTool].
	 *
	 * A [ClientPerson] object matches the input text query if the person's full name contains the string,
	 * ignoring case.
	 *
	 * @return The resulting list of matching [ClientPerson] objects
	 */
	fun searchEnabledPeople(): List<ClientPerson> {
		return SearchTool.searchEnabledPeople()
	}

	/**
	 * Searches all of the [ClientEvent] objects in the [DataCache] that are enabled by the current filters
	 * in the [com.griffinbholt.familymapclient.model.Settings] singleton using the [SearchTool].
	 *
	 * A [ClientEvent] object matches the input text query if the event's city, country, event type, and/or year
	 * contains the string, ignoring case.
	 *
	 * @return The resulting list of matching [ClientEvent] objects
	 */
	fun searchEnabledEvents(): List<ClientEvent> {
		return SearchTool.searchEnabledEvents()
	}

	/**
	 * Clears all of the data stored in the [DataCache].
	 */
	fun clear() {
		clearUserInfo()
		clearPeople()
		clearEvents()
	}

	private fun clearUserInfo() {
		personID = null
		user = null
		authToken = null
	}

	private fun clearPeople() {
		motherSideFemales.clear()
		motherSideMales.clear()
		fatherSideFemales.clear()
		fatherSideMales.clear()
	}

	private fun clearEvents() {
		motherSideFemaleEvents.clear()
		motherSideMaleEvents.clear()
		fatherSideFemaleEvents.clear()
		fatherSideMaleEvents.clear()
	}

	/**
	 * @return A [List] of all of the [ClientPerson] objects enabled by the current settings filters.
	 */
	fun enabledPersons(): List<ClientPerson> {
		val enabledPerson: MutableList<ClientPerson> = ArrayList()

		if (Settings.motherSideFilter) {
			enabledPerson.addAll(motherSideFemales)
			enabledPerson.addAll(motherSideMales)
		}

		if (Settings.fatherSideFilter) {
			enabledPerson.addAll(fatherSideFemales)
			enabledPerson.addAll(fatherSideMales)
		}

		return enabledPerson
	}

	/**
	 * @return A [List] of all of the [ClientEvent] objects enabled by the current settings filters.
	 */
	fun enabledEvents(): List<ClientEvent> {
		val enabledEvents: MutableList<ClientEvent> = ArrayList()

		if (Settings.femaleEventFilter) {
			enabledEvents.addAll(immediateFamilyFemaleEvents)

			if (Settings.motherSideFilter) {
				enabledEvents.addAll(motherSideFemaleEvents)
			}

			if (Settings.fatherSideFilter) {
				enabledEvents.addAll(fatherSideFemaleEvents)
			}
		}

		if (Settings.maleEventFilter) {
			enabledEvents.addAll(immediateFamilyMaleEvents)

			if (Settings.motherSideFilter) {
				enabledEvents.addAll(motherSideMaleEvents)
			}

			if (Settings.fatherSideFilter) {
				enabledEvents.addAll(fatherSideMaleEvents)
			}
		}

		return enabledEvents
	}
}