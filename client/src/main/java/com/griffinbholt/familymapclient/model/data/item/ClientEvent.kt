package com.griffinbholt.familymapclient.model.data.item

import com.google.android.gms.maps.model.LatLng
import shared.model.Event
import shared.model.ServerEvent
import java.io.Serializable
import java.util.*

class ClientEvent(
		val person: ClientPerson? = null,
		event: ServerEvent
) : Event(event.latitude, event.longitude, event.country, event.city, event.eventType, event.year),
		Comparable<ClientEvent>, Serializable {

	init {
		eventType.eventName = eventType.eventName.toUpperCase(Locale.ROOT)
	}

	fun latLng(): LatLng = LatLng(latitude, longitude)

	fun description() = "${eventType.eventName}: $city, $country ($year)"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ClientEvent) return false
		return super.equals(other) && (person == other.person)
	}

	override fun hashCode(): Int {
		var result = super.hashCode()
		result = 31 * result + (person?.hashCode() ?: 0)
		return result
	}

	override fun compareTo(other: ClientEvent): Int {
		return this.year.compareTo(other.year)
	}
}