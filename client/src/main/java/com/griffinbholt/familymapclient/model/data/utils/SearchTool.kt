package com.griffinbholt.familymapclient.model.data.utils

import com.griffinbholt.familymapclient.model.data.DataCache
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import java.util.*

/**
 * A singleton util object that assists the [DataCache] in executing textual search queries.
 *
 * @author griffinbholt
 */
object SearchTool {

	private var textQuery: String? = null

	private val foundPeople: MutableList<ClientPerson> = ArrayList()
	private val foundEvents: MutableList<ClientEvent> = ArrayList()

	/**
	 * Sets the text query against which [ClientPerson] and [ClientEvent] objects in the [DataCache]
	 * will be checked
	 *
	 * @param textQuery A [String] for which matching [ClientPerson] and [ClientEvent] objects will be sought
	 */
	fun setTextQuery(textQuery: String) {
		SearchTool.textQuery = textQuery
	}

	/**
	 * Searches all of the [ClientPerson] objects in the [DataCache] that are enabled by the current filters
	 * in the [com.griffinbholt.familymapclient.model.Settings] singleton.
	 *
	 * A [ClientPerson] object matches the input text query if the person's full name contains the string,
	 * ignoring case.
	 *
	 * @return The resulting list of matching [ClientPerson] objects
	 */
	fun searchEnabledPeople(): List<ClientPerson> {
		foundPeople.clear()

		checkImmediateFamily()

		checkPersons(DataCache.enabledPersons())

		return foundPeople.sorted()
	}

	private fun checkImmediateFamily() {
		checkPerson(DataCache.user!!)
		DataCache.user!!.spouse?.let { checkPerson(it) }
	}

	private fun checkPersons(persons: List<ClientPerson>) {
		for (person in persons) {
			checkPerson(person)
		}
	}

	private fun checkPerson(person: ClientPerson) {
		if (person.fullName().contains(textQuery!!, true)) {
			foundPeople.add(person)
		}
	}

	/**
	 * Searches all of the [ClientEvent] objects in the [DataCache] that are enabled by the current filters
	 * in the [com.griffinbholt.familymapclient.model.Settings] singleton.
	 *
	 * A [ClientEvent] object matches the input text query if the event's city, country, event type, and/or year
	 * contains the string, ignoring case.
	 *
	 * @return The resulting list of matching [ClientEvent] objects
	 */
	fun searchEnabledEvents(): List<ClientEvent> {
		foundEvents.clear()

		checkEvents(DataCache.enabledEvents())

		return foundEvents.sorted()
	}

	private fun checkEvents(events: List<ClientEvent>) {
		for (event in events) {
			checkEvent(event)
		}
	}

	private fun checkEvent(event: ClientEvent) {
		if (event.description().contains(textQuery!!, true)) {
			foundEvents.add(event)
		}
	}
}