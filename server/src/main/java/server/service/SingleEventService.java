package server.service;

import server.exception.DataAccessException;
import shared.model.AuthToken;
import shared.model.ServerEvent;
import shared.result.EventResult;

/**
 * A <code>SingleEventService</code> object manages the functionality to request an event
 * @author griffinbholt
 */
public final class SingleEventService extends EventsOnlyService {
    /**
     * Returns the single ServerEvent object, in the form of {@link shared.result.EventResult},
     * with the specified eventID. Requires an authorization token.
     * @param eventID The eventID for the requested {@link ServerEvent ServerEvent} object
     * @param authToken The authorization token of the party requesting the {@link ServerEvent ServerEvent}
     *                  object
     * @return The result of the request (containing the requested {@link ServerEvent ServerEvent} object
     *         if successful)
     */
    public EventResult getEvent(String eventID, AuthToken authToken) {
        try {
            super.openConnection();
            ServerEvent event = this.getEventFromDatabase(eventID, authToken);
            super.commitChanges();
            EventResult success = EventResult.newSuccess(event);
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            EventResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            super.closeConnection();
        }
    }

    private ServerEvent getEventFromDatabase(String eventID, AuthToken authToken) throws DataAccessException {
        return eventDao.getAuthorizedEvent(eventID, authToken);
    }

    @Override
    protected EventResult reportError(Exception e) {
        String errorMessage = "Error: ServerEvent request failed. " + e.getMessage();
        logError(errorMessage);
        return EventResult.newFailure(errorMessage);
    }
}
