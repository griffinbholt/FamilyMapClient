package com.griffinbholt.familymapclient.model.data.utils

import com.griffinbholt.familymapclient.model.data.DataCache
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import java.util.*

object SearchTool : Search {
    private var textQuery: String? = null

    private val foundPeople: MutableSet<ClientPerson> = TreeSet()
    private val foundEvents: MutableSet<ClientEvent> = TreeSet()

    override fun setTextQuery(textQuery: String) {
        SearchTool.textQuery = textQuery
    }

    override fun searchPeople(): List<ClientPerson> {
        foundPeople.clear()

        checkPerson(DataCache.user!!)
        searchPersonCaches()

        return foundPeople.sorted()
    }

    private fun searchPersonCaches() {
        val personCaches : List<List<ClientPerson>> = DataCache.enabledPersonCaches()

        for (cache in personCaches) {
            searchPersonCache(cache)
        }
    }

    private fun searchPersonCache(cache: List<ClientPerson>) {
        for (person in cache) {
            checkPerson(person)
        }
    }

    private fun checkPerson(person: ClientPerson) {
        if (person.fullName().contains(textQuery!!, true)) {
            foundPeople.add(person)
        }
    }

    override fun searchEvents(): List<ClientEvent> {
        foundEvents.clear()

        searchEventCaches()

        return foundEvents.sorted()
    }

    private fun searchEventCaches() {
        val eventCaches: List<List<ClientEvent>> = DataCache.enabledEventsCaches()

        for (cache in eventCaches) {
            searchEventCache(cache)
        }
    }

    private fun searchEventCache(cache: List<ClientEvent>) {
        for (event in cache) {
            if (event.description().contains(textQuery!!, true)) {
                foundEvents.add(event)
            }
        }
    }
}