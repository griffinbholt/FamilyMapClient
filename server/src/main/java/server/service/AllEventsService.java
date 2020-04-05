package server.service;

import server.exception.DataAccessException;
import shared.model.AuthToken;
import shared.model.ServerEvent;
import shared.result.AllEventsResult;

import java.util.List;

/**
 * A <code>AllEventsService</code> object manages requests for all events connected to a user and his/her ancestors
 * @author griffinbholt
 */
public final class AllEventsService extends EventsOnlyService {
    /**
     * ​Returns all {@link ServerEvent ServerEvent} objects connected to the user with the
     * specified authorization token and his/her ancestors, in the form of
     * an {@link shared.result.AllEventsResult AllEventsResult} object, that matches the specified personID.
     * Requires an authorization token.
     * @param authToken The authorization token of the party requesting
     * the {@link ServerEvent ServerEvent} objects
     * @return The result of the request (containing the requested {@link ServerEvent ServerEvent} objects
     *         if successful)
     */
    public AllEventsResult getAllEvents(AuthToken authToken) {
        try {
            super.openConnection();
            List<ServerEvent> familyMembers = this.getEventsFromDatabase(authToken);
            super.commitChanges();
            AllEventsResult success = AllEventsResult.newSuccess(familyMembers);
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            AllEventsResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            super.closeConnection();
        }
    }

    private List<ServerEvent> getEventsFromDatabase(AuthToken authToken) throws DataAccessException {
        return eventDao.getAllConnectedEvents(authToken);
    }

    @Override
    protected AllEventsResult reportError(Exception e) {
        String errorMessage = "Error: Family member events request failed. " + e.getMessage();
        logError(errorMessage);
        return AllEventsResult.newFailure(errorMessage);
    }
}
