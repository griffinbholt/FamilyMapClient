package com.griffinbholt.familymapclient.model.data.utils

import com.griffinbholt.familymapclient.model.data.DataCache
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import java.util.*

object SearchTool {
	private var textQuery: String? = null

	private val foundPeople: MutableList<ClientPerson> = ArrayList()
	private val foundEvents: MutableList<ClientEvent> = ArrayList()

	fun setTextQuery(textQuery: String) {
		SearchTool.textQuery = textQuery
	}

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