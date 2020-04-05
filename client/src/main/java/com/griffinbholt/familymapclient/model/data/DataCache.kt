package com.griffinbholt.familymapclient.model.data

import com.griffinbholt.familymapclient.controller.utils.IconGenerator
import com.griffinbholt.familymapclient.model.data.item.ClientEvent
import com.griffinbholt.familymapclient.model.data.item.ClientPerson
import com.griffinbholt.familymapclient.model.data.utils.Search
import com.griffinbholt.familymapclient.model.data.utils.SearchTool
import shared.model.*
import java.util.*
import kotlin.collections.ArrayList

// TODO - More detailed error checking & logging
// TODO - Split up functions

object DataCache : Search {
    private enum class SideOfFamily {
        MOTHER, FATHER
    }

    var personID : String? = null

    var user : ClientPerson? = null

    var firstName : String? = null
        private set
        get() = user?.firstName

    var lastName: String? = null
        private set
        get() = user?.lastName

    var authToken: AuthToken? = null

    var motherSideFemales: ArrayList<ClientPerson> = ArrayList()
    var motherSideMales: ArrayList<ClientPerson> = ArrayList()
    var fatherSideFemales: ArrayList<ClientPerson> = ArrayList()
    var fatherSideMales: ArrayList<ClientPerson> = ArrayList()

    var maleEvents: ArrayList<ClientEvent> = ArrayList()
    var femaleEvents: ArrayList<ClientEvent> = ArrayList()

    private var familyMembersTmpCache: List<ServerPerson>? = null
    private var familyEventsTmpCache: List<ServerEvent>? = null

    fun loadFamilyMembers(inFamilyMembers: List<ServerPerson>) {
        familyMembersTmpCache = inFamilyMembers

        val userPerson : ServerPerson = findServerPerson(personID)!!

        val serverMother : ServerPerson? = findServerPerson(userPerson.motherID)
        val serverFather : ServerPerson? = findServerPerson(userPerson.fatherID)

        val clientMother: ClientPerson? = serverMother?.let { recursivelyLoadFamilyMembers(it, SideOfFamily.MOTHER) }
        val clientFather: ClientPerson? = serverFather?.let { recursivelyLoadFamilyMembers(it, SideOfFamily.FATHER) }

        connectSpouses(clientMother, clientFather)

        user = ClientPerson(clientMother, clientFather, userPerson)

        connectChildToParents(clientFather, user!!, clientMother)
    }

    private fun findServerPerson(personID: String?) : ServerPerson? {
        return familyMembersTmpCache!!.find { it.personID == personID }
    }

    private fun recursivelyLoadFamilyMembers(person: ServerPerson?, sideOfFamily: SideOfFamily) : ClientPerson? {
        // Base Case #1 - The person does not exist
        if (person == null) {
            return null
        }

        val serverMother : ServerPerson? = findServerPerson(person.motherID)
        val serverFather : ServerPerson? = findServerPerson(person.fatherID)

        val clientMother: ClientPerson? = serverMother?.let { recursivelyLoadFamilyMembers(it, sideOfFamily) }
        val clientFather: ClientPerson? = serverFather?.let { recursivelyLoadFamilyMembers(it, sideOfFamily) }

        connectSpouses(clientMother, clientFather)

        val clientPerson = ClientPerson(clientMother, clientFather, person)

        connectChildToParents(clientFather, clientPerson, clientMother)

        addToCorrectPersonCache(clientPerson, sideOfFamily)

        return clientPerson
    }

    private fun connectChildToParents(clientFather: ClientPerson?, clientPerson: ClientPerson, clientMother: ClientPerson?) {
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

    @JvmStatic
    fun loadFamilyEvents(inFamilyEvents: List<ServerEvent>) {
        val possibleEventTypes : MutableSet<EventType> = TreeSet()

        familyEventsTmpCache = inFamilyEvents

        for (event in familyEventsTmpCache!!) {
            val person : ClientPerson = findClientPerson(event.personID)!!

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

    private fun addToCorrectEventCache(person: ClientPerson, clientEvent: ClientEvent) {
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        when (person.gender) {
            Gender.FEMALE -> femaleEvents.add(clientEvent)
            Gender.MALE -> maleEvents.add(clientEvent)
        }
    }

    private fun findClientPerson(personID: String) : ClientPerson? {
        var clientPerson: ClientPerson? = checkUser(personID)

        if (clientPerson == null) {
            clientPerson = checkEachPersonCache(personID, clientPerson)
        }

        return clientPerson
    }

    private fun checkEachPersonCache(personID: String, clientPerson: ClientPerson?) : ClientPerson? {
        val personCaches: List<List<ClientPerson>> = listOf(motherSideFemales, motherSideMales, fatherSideFemales, fatherSideMales)
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

    private fun checkPersonCache(personID: String, cache: List<ClientPerson>) : ClientPerson? {
        for (person in cache) {
            if (person.personID == personID) {
                return person
            }
        }

        return null
    }

    override fun setTextQuery(textQuery: String) {
        SearchTool.setTextQuery(textQuery)
    }

    override fun searchPeople(): List<ClientPerson> {
        return SearchTool.searchPeople()
    }

    override fun searchEvents(): List<ClientEvent> {
        return SearchTool.searchEvents()
    }

    fun clear() {
        personID = null

        user = null

        firstName = null
        lastName = null

        authToken = null

        motherSideFemales.clear()
        motherSideMales.clear()
        fatherSideFemales.clear()
        fatherSideMales.clear()

        maleEvents.clear()
        femaleEvents.clear()
    }

    fun enabledPersonCaches() : List<List<ClientPerson>> {
        val enabledPersonCaches : MutableList<List<ClientPerson>> = ArrayList()

        if (Settings.motherSideFilter) {
            enabledPersonCaches.add(motherSideFemales)
            enabledPersonCaches.add(motherSideMales)
        }

        if (Settings.fatherSideFilter) {
            enabledPersonCaches.add(fatherSideFemales)
            enabledPersonCaches.add(fatherSideMales)
        }

        return enabledPersonCaches
    }

    fun enabledEventsCaches() : List<List<ClientEvent>> {
        val enabledEventsCaches : MutableList<List<ClientEvent>> = ArrayList()

        if (Settings.femaleEventFilter) {
            enabledEventsCaches.add(femaleEvents)
        }

        if (Settings.maleEventFilter) {
            enabledEventsCaches.add(maleEvents)
        }

        return enabledEventsCaches
    }
}