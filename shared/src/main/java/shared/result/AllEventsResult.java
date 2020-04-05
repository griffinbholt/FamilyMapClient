package shared.result;

import shared.model.ServerEvent;
import shared.model.ServerPerson;

import java.util.Collections;
import java.util.List;

/**
 * The result of a request for ALL events related to ALL family members of the current user
 * @author griffinbholt
 */
public final class AllEventsResult extends Result {
    /**
     * An array of {@link ServerPerson Person} objects, representing ALL events
     * related to ALL family members of the current user
     */
    private final List<ServerEvent> data;

    /**
     * Factory method that creates a new <code>AllEventsResult</code> object,
     * with <code>success</code> set to <code>true</code>
     * @param events An array of {@link ServerEvent ServerEvent} objects, representing ALL events
     *               related to ALL family members of the current user
     * @return The new success <code>AllEventsResult</code> object
     */
    public static AllEventsResult newSuccess(List<ServerEvent> events) {
        return new AllEventsResult(events);
    }

    private AllEventsResult(List<ServerEvent> events) {
        this(events, "Successfully found " + events.size() + " events for the input authorization token.", true);
    }

    /**
     * Factory method that creates a new <code>AllEventsResult</code> object,
     * with <code>success</code> set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed events query
     * @return  The new failure <code>AllEventsResult</code> object
     */
    public static AllEventsResult newFailure(String errorMessage) {
        return new AllEventsResult(errorMessage);
    }

    private AllEventsResult(String errorMessage) {
        this(null, errorMessage, false);
    }

    private AllEventsResult(List<ServerEvent> events, String message, boolean success) {
        super(message, success);
        this.data = events;
    }

    // Getter
    public List<ServerEvent> getData() {
        return (null != data) ? Collections.unmodifiableList(data) : null;
    }
}
