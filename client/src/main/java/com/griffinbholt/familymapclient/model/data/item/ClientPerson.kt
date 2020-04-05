package com.griffinbholt.familymapclient.model.data.item

import shared.model.Person
import shared.model.ServerPerson
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class ClientPerson(val mother: ClientPerson?,
                   val father: ClientPerson?,
                   person: ServerPerson) :
                   Person(person.personID, person.firstName, person.lastName, person.gender),
                   Comparable<ClientPerson>, Serializable {

    var spouse: ClientPerson? = null
    val children: ArrayList<ClientPerson> = ArrayList()
    val events: SortedSet<ClientEvent> = TreeSet()

    fun fullName() = "$firstName $lastName"

    fun addChild(child: ClientPerson) {
        children.add(child)
    }

    fun addEvent(event: ClientEvent) {
        events.add(event)
    }

    override fun compareTo(other: ClientPerson): Int {
        val thisLastFirst =  "$lastName $firstName"
        val otherLastFirst = "${other.lastName} ${other.firstName}"

        return thisLastFirst.compareTo(otherLastFirst, true)
    }
}