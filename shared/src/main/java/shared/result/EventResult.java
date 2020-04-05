package shared.result;

import shared.model.ServerEvent;

/**
 * The result of an event request
 * Inherits from {@link Result Result}.
 * @author griffinbholt
 */
public final class EventResult extends Result {
    private String associatedUsername;
    private String eventID;
    private String personID;
    private Double latitude;
    private Double longitude;
    private String country;
    private String city;
    private String eventType;
    private Integer year;

    /**
     * Factory method that creates a new <code>EventResult</code> object,
     * with <code>success</code> set to <code>true</code>
     * @param event The {@link ServerEvent ServerEvent} object that was successfully found
     * @return The new success <code>EventResult</code> object
     */
    public static EventResult newSuccess(ServerEvent event) {
        return new EventResult(event);
    }

    private EventResult(ServerEvent event) {
        this(event, "Successfully found event: {" + event.getEventID() + "}.", true);
    }

    /**
     * Factory method that creates a new <code>EventResult</code> object,
     * with <code>success</code> set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed event query
     * @return  The new failure <code>EventResult</code> object
     */
    public static EventResult newFailure(String errorMessage) {
        return new EventResult(errorMessage);
    }

    private EventResult(String errorMessage) {
        this(null, errorMessage, false);
    }

    private EventResult(ServerEvent event, String message, boolean success) {
        super(message, success);

        if (null != event) {
            this.associatedUsername = event.getAssociatedUsername();
            this.eventID = event.getEventID();
            this.personID = event.getPersonID();
            this.latitude = event.getLatitude();
            this.longitude = event.getLongitude();
            this.country = event.getCountry();
            this.city = event.getCity();
            this.eventType = event.getTypeName();
            this.year = event.getYearAsInt();
        }
    }

    // Getters
    public ServerEvent getEvent() {
        return (null != eventID) ?
                new ServerEvent(eventID, associatedUsername, personID, latitude, longitude, country, city, eventType, year) :
                null;

    }
}
