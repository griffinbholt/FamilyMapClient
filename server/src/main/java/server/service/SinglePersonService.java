package server.service;

import server.exception.DataAccessException;
import shared.model.AuthToken;
import shared.model.ServerPerson;
import shared.result.PersonResult;

/**
 * A <code>SinglePersonService</code> object manages person requests
 * @author griffinbholt
 */
public final class SinglePersonService extends PersonsOnlyService {
    /**
     * ​Returns a single {@link ServerPerson Person} object, in the form of a
     * {@link shared.result.PersonResult PersonResult} object, that matches
     * the specified personID. Requires an authorization token.
     * @param personID The personID for the requested {@link ServerPerson Person} object
     * @param authToken The authorization token of the party requesting the {@link ServerPerson Person}
     *                  object
     * @return The result of the request (containing the requested {@link ServerPerson Person} object if
     *         successful)
     */
    public PersonResult getPerson(String personID, AuthToken authToken) {
        try {
            super.openConnection();
            ServerPerson person = this.getPersonFromDatabase(personID, authToken);
            super.commitChanges();
            PersonResult success = PersonResult.newSuccess(person);
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            PersonResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            super.closeConnection();
        }
    }

    private ServerPerson getPersonFromDatabase(String personID, AuthToken authToken) throws DataAccessException {
        return personDao.getAuthorizedPerson(personID, authToken);
    }

    @Override
    protected PersonResult reportError(Exception e) {
        String errorMessage = "Error: Person request failed. " + e.getMessage();
        logError(errorMessage);
        return PersonResult.newFailure(errorMessage);
    }
}
