package com.griffinbholt.familymapclient.model.data.item

import shared.model.Person
import shared.model.ServerPerson
import java.io.Serializable

/**
 * A subclass of the generic [Person] class used by both the server and the client.
 * In addition to the standard [Person] data, it also includes functionality required by the client.
 *
 * Implements [Comparable<ClientPerson>][Comparable] and [Serializable].
 *
 * WARNING: Do not place in ordered [Set] collections. The [compareTo] function only orders [ClientPerson]
 * objects lexicographically according to the [ClientPerson]'s name, and therefore,
 * will cause overlap when used in ordered [Set] collections.
 * Unordered [Set] collections (e.g., [HashSet]) can be used.
 *
 * @param sideOfFamily A [SideOfFamily] enum, representing the side of the user's family
 * 				       to which the [ClientPerson] belongs
 * @param mother The mother of the [ClientPerson], in the form of a [ClientPerson] object
 * @param father The father of the [ClientPerson], in the form of a [ClientPerson] object
 * @param person The input [ServerPerson] object, the [Person] data of which will be equivalent to
 * 				 the new [ClientPerson] object
 *
 * @author griffinbholt
 */
class ClientPerson(
		val sideOfFamily: SideOfFamily,
		val mother: ClientPerson?,
		val father: ClientPerson?,
		person: ServerPerson
) : Person(person.personID, person.firstName, person.lastName, person.gender), Comparable<ClientPerson>, Serializable {

	/**
	 * A [ClientPerson] object, representing the spouse of the instance [ClientPerson]
	 */
	var spouse: ClientPerson? = null

	/**
	 * A [MutableList] of [ClientPerson] objects, representing the children of the instance [ClientPerson]
	 */
	val children: MutableList<ClientPerson> = ArrayList()

	/**
	 * A [MutableList] of [events][ClientEvent] related to the [ClientPerson]
	 */
	val events: MutableList<ClientEvent> = ArrayList()

	/**
	 * The full name of the [ClientPerson], in the following format: "{FirstName} {LastName}"
	 */
	fun fullName(): String = "$firstName $lastName"

	/**
	 * Adds a child to the internal list of the [ClientPerson]'s children
	 *
	 * @param child A [ClientPerson] object representing a child of the instance [ClientPerson]
	 */
	fun addChild(child: ClientPerson) {
		children.add(child)
	}

	/**
	 * Adds an event to the internal list of [ClientEvent]s associated with the [ClientPerson]
	 *
	 * @param event A [ClientEvent] that is associated with the [ClientPerson]
	 */
	fun addEvent(event: ClientEvent) {
		events.add(event)
	}

	/**
	 * Tests if the input [ClientPerson] object is equal to the current instance.
	 *
	 * @param other Input [Any] object to be tested for equality with the current [ClientPerson] instance
	 * @return true, if the two events have all of the same [Person] data; false, if otherwise
	 */
	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	/**
	 * Generates a hashcode for the [ClientPerson] object.
	 * @return The hashcode
	 */
	override fun hashCode(): Int {
		return super.hashCode()
	}

	/**
	 * Lexicographically compares the full name of the [ClientPerson] instance
	 * against the full name of the input [ClientPerson]. The [ClientPerson] objects
	 * are compared by their last names first, and then by their first names.
	 *
	 * @param other A [ClientPerson] object, against which to compare the current [ClientPerson] instance
	 * @return Zero, if the names are the same;
	 * 		   A negative number, if the instance's name is lexicographically less than the other's name;
	 * 		   A negative number, if the instance's name is lexicographically more than the other's name;
	 */
	override fun compareTo(other: ClientPerson): Int {
		val thisLastFirst = "$lastName $firstName"
		val otherLastFirst = "${other.lastName} ${other.firstName}"

		return thisLastFirst.compareTo(otherLastFirst, true)
	}
}