package com.griffinbholt.familymapclient.model.data.item

import shared.model.Event
import shared.model.ServerEvent
import java.io.Serializable
import java.util.*

class ClientEvent(val person: ClientPerson? = null,
                  event: ServerEvent) :
                  Event(event.latitude, event.longitude, event.country, event.city,
                          event.eventType, event.year),
                  Comparable<ClientEvent>, Serializable {

    init {
        eventType.eventName = eventType.eventName.toUpperCase(Locale.ROOT)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientEvent) return false
        return super.equals(other) && (person == other.person)
    }

    /**
     * Generates a hashcode for the `ClientEvent` object
     * @return The hashcode
     */
    override fun hashCode(): Int {
        return Objects.hash(person, latitude, longitude, country, city, eventType, year)
    }

    override fun compareTo(other: ClientEvent): Int {
        return this.year.compareTo(other.year)
    }

    fun description() = "${eventType.eventName}: $city, $country ($year)"
}