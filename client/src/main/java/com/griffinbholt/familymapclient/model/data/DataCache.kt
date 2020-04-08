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

object DataCache {
	var personID: String? = null
	var authToken: AuthToken? = null
	var user: ClientPerson? = null

	fun firstName(): String? = user?.firstName
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

	fun loadFamilyMembers(inFamilyMembers: List<ServerPerson>) {
		familyMembersTmpCache = inFamilyMembers

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

	fun loadFamilyEvents(inFamilyEvents: List<ServerEvent>) {
		val possibleEventTypes: MutableSet<EventType> = TreeSet()

		familyEventsTmpCache = inFamilyEvents

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

	fun setTextQuery(textQuery: String) {
		SearchTool.setTextQuery(textQuery)
	}

	fun searchEnabledPeople(): List<ClientPerson> {
		return SearchTool.searchEnabledPeople()
	}

	fun searchEnabledEvents(): List<ClientEvent> {
		return SearchTool.searchEnabledEvents()
	}

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