package com.griffinbholt.familymapclient.model.data.item

import shared.model.Person
import shared.model.ServerPerson
import java.io.Serializable

class ClientPerson(
		val sideOfFamily: SideOfFamily,
		val mother: ClientPerson?,
		val father: ClientPerson?,
		person: ServerPerson
) : Person(person.personID, person.firstName, person.lastName, person.gender), Comparable<ClientPerson>, Serializable {

	var spouse: ClientPerson? = null
	val children: MutableList<ClientPerson> = ArrayList()
	val events: MutableList<ClientEvent> = ArrayList()

	fun fullName() = "$firstName $lastName"

	fun addChild(child: ClientPerson) {
		children.add(child)
	}

	fun addEvent(event: ClientEvent) {
		events.add(event)
	}

	override fun compareTo(other: ClientPerson): Int {
		val thisLastFirst = "$lastName $firstName"
		val otherLastFirst = "${other.lastName} ${other.firstName}"

		return thisLastFirst.compareTo(otherLastFirst, true)
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}
}