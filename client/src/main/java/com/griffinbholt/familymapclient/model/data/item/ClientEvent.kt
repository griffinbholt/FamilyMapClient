package com.griffinbholt.familymapclient.model.data.item

import com.google.android.gms.maps.model.LatLng
import shared.model.Event
import shared.model.ServerEvent
import java.io.Serializable
import java.util.*

/**
 * A subclass of the generic [Event] class used by both the server and the client.
 * In addition to the standard [Event] data, it also includes functionality required by the client.
 *
 * Implements [Comparable<ClientEvent>][Comparable] and [Serializable].
 *
 * WARNING: Do not place in ordered [Set] collections. The [compareTo] function only orders [ClientEvent]
 * objects chronologically, and therefore, will cause overlap when used in ordered [Set] collections.
 * Unordered [Set] collections (e.g., [HashSet]) can be used.
 *
 * @param person The [ClientPerson] associated with the [ClientEvent]
 * @param event The input [ServerEvent] object, the [Event] data of which will be equivalent
 * 				to the new [ClientEvent] object
 *
 * @author griffinbholt
 */
class ClientEvent(
		val person: ClientPerson? = null,
		event: ServerEvent
) : Event(event.latitude, event.longitude, event.country, event.city, event.eventType, event.year),
		Comparable<ClientEvent>, Serializable {

	init {
		eventType.eventName = eventType.eventName.toUpperCase(Locale.ROOT)
	}

	/**
	 * @return A [LatLng] object representing the latitude and longitude of the [ClientEvent]
	 */
	fun latLng(): LatLng = LatLng(latitude, longitude)

	/**
	 * @return The [String] description of the [ClientEvent] in the following format: "EVENT TYPE: City, Country {YEAR}"
	 */
	fun description(): String = "${eventType.eventName}: $city, $country ($year)"

	/**
	 * Tests if the input [ClientEvent] object is equal to the current instance.
	 *
	 * @param other Input [Any] object to be tested for equality with the current [ClientEvent] instance
	 * @return true, if the two events have all of the same information; false, if otherwise
	 */
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ClientEvent) return false
		return super.equals(other) && (person == other.person)
	}

	/**
	 * Generates a hashcode for the [ClientEvent] object.
	 *
	 * @return The hashcode
	 */
	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + (person?.hashCode() ?: 0)
		return result
	}

	/**
	 * Chronologically compares the instance [ClientEvent] against the input [ClientEvent] object.
	 *
	 * @param other A [ClientEvent] object, against which to compare the current [ClientEvent] instance
	 * @return Zero, if the [year][java.time.Year]s are the same;
	 * 		   A negative number, if the instance's [java.time.Year] is less than the other [java.time.Year];
	 * 		   A positive number, if the instance's [java.time.Year] is greater than the other [java.time.Year]
	 */
	override fun compareTo(other: ClientEvent): Int {
		return this.year.compareTo(other.year)
	}
}