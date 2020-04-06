package shared.model;

import java.io.Serializable;
import java.time.Year;
import java.util.Objects;

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

    /**
     * Tests if the input <code>Event</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>Event</code> instance
     * @return true, if the two events have all of the same information; false, if otherwise
     */
    @SuppressWarnings("OverlyComplexBooleanExpression")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerEvent)) return false;
        ServerEvent event = (ServerEvent) o;
        return 0 == Double.compare(event.getLatitude(), getLatitude()) &&
                0 == Double.compare(event.getLongitude(), getLongitude()) &&
                getCountry().equals(event.getCountry()) &&
                getCity().equals(event.getCity()) &&
                getEventType().equals(event.getEventType()) &&
                getYear().equals(event.getYear());
    }

    /**
     * Generates a hashcode for the <code>Event</code> object
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(),
                getLongitude(), getCountry(), getCity(), getEventType(), getYear());
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
