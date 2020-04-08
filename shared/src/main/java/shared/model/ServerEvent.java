package shared.model;

import java.time.Year;
import java.util.Objects;
import java.util.UUID;

/**
 * A Plain Old Java Object to represent genealogical events utilized by the server
 * @author griffinbholt
 */
public class ServerEvent extends Event {
    /**
     * The username of the user to whom the event is linked
     */
    private final String associatedUsername;

    /**
     * A unique event ID
     */
    private final String eventID;

    /**
     * The ID of the {@link ServerPerson Person} to whom the event occurred
     */
    private final String personID;

    /**
     * Creates a new <code>ServerEvent</code> object with the instance variable values input through the parameters
     * @param eventID Input unique event ID
     * @param associatedUsername Input username
     * @param personID Input person ID
     * @param latitude Input latitude of the event
     * @param longitude Input longitude of the event
     * @param country Input country in which the event occurred
     * @param city Input city in which the event occurred
     * @param typeName Input type of event that occurred
     * @param year Input year in which the event occurred
     */
    public ServerEvent(String eventID, String associatedUsername, String personID, double latitude,
                       double longitude, String country, String city, String typeName, int year) {
        this(eventID, associatedUsername, personID, latitude, longitude, country, city,
                EventType.generate(typeName), Year.of(year));
    }

    /**
     * Creates a new <code>ServerEvent</code> object with the instance variable values input through the parameters.
     * The eventID will be generated for the event.
     * @param associatedUsername Input username
     * @param personID Input person ID
     * @param latitude Input latitude of the event
     * @param longitude Input longitude of the event
     * @param country Input country in which the event occurred
     * @param city Input city in which the event occurred
     * @param eventType Input type of event that occurred, in the form of
     *                  an {@link EventType EventType} enum
     * @param year Input year in which the event occurred
     */
    public ServerEvent(String associatedUsername, String personID, double latitude, double longitude, String country,
                       String city, EventType eventType, Year year) {
        this(null, associatedUsername, personID, latitude, longitude, country, city, eventType, year);
    }

    /**
     * Creates a new <code>ServerEvent</code> object with the instance variable values input through the parameters
     * @param eventID Input unique event ID
     * @param associatedUsername Input username
     * @param personID Input person ID
     * @param latitude Input latitude of the event
     * @param longitude Input longitude of the event
     * @param country Input country in which the event occurred
     * @param city Input city in which the event occurred
     * @param eventType Input type of event that occurred
     * @param year Input year in which the event occurred
     */
    public ServerEvent(String eventID, String associatedUsername, String personID, double latitude, double longitude,
                       String country, String city, EventType eventType, Year year) {
        super(latitude, longitude, country, city, eventType, year);

        this.eventID = Objects.requireNonNullElseGet(eventID, () ->
                generateUniqueEventID(associatedUsername, personID, latitude, longitude, country, city,
                                      eventType, year));

        this.associatedUsername = associatedUsername;
        this.personID = personID;
    }

    private String generateUniqueEventID(String username, String personID, double latitude, double longitude,
                                         String country, String city, EventType type, Year year) {
        final int HASH_FACTOR = 31;
        double hash = username.hashCode() + personID.hashCode() + latitude + longitude + country.hashCode() +
                      city.hashCode() + type.hashCode() + year.getValue();
        hash *= HASH_FACTOR;
        return (Double.toString(hash) + UUID.randomUUID());
    }

    /**
     * Generates a string representation of the genealogical event
     * @return The string representation of the user and his/her information
     */
    @Override
    public String toString() {
        return "ServerEvent{" +
                "eventID='" + eventID + '\'' +
                ", userName='" + associatedUsername + '\'' +
                ", personID='" + personID + '\'' +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                ", country='" + getCountry() + '\'' +
                ", city='" + getCity() + '\'' +
                ", type=" + getEventType() +
                ", year=" + getYear() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerEvent)) return false;
        ServerEvent event = (ServerEvent) o;
        return super.equals(o) &&
                getAssociatedUsername().equals(event.getAssociatedUsername()) &&
                getEventID().equals(event.getEventID()) &&
                getPersonID().equals(event.getPersonID());
    }

    /**
     * Generates a hashcode for the <code>ServerEvent</code> object
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getAssociatedUsername(), getEventID(), getPersonID(), getLatitude(),
                getLongitude(), getCountry(), getCity(), getEventType(), getYear());
    }

    // Getters
    public String getEventID() {
        return this.eventID;
    }

    public String getAssociatedUsername() {
        return this.associatedUsername;
    }

    public String getPersonID() {
        return this.personID;
    }
}
