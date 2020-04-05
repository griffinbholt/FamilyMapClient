package shared.model;

import java.io.Serializable;
import java.time.Year;

public class Event implements Serializable {
    /**
     * The latitude at which the event occurred
     */
    private final double latitude;

    /**
     * The longitude at which the event occurred
     */
    private final double longitude;

    /**
     * The country in which the event occurred
     */
    private final String country;

    /**
     * The city in which the event occurred
     */
    private final String city;

    /**
     * The type of event that occurred
     */
    private final EventType eventType;

    /**
     * The year in which the event occurred
     */
    private final Year year;

    public Event(double latitude, double longitude, String country, String city, EventType eventType,
          Year year) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getCountry() {
        return this.country;
    }

    public String getCity() {
        return this.city;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String getTypeName() { return this.eventType.getEventName(); }

    public Year getYear() {
        return this.year;
    }

    public int getYearAsInt() { return this.year.getValue(); }
}
