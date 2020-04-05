package server.service;

import server.dao.AuthTokenDao;
import server.dao.EventDao;
import server.dao.PersonDao;
import server.exception.DataAccessException;
import shared.model.ServerPerson;
import shared.model.User;
import shared.request.FillRequest;
import shared.result.FillResult;

/**
 * A <code>FillService</code> object manages the functionality to fill the database with generated genealogical data
 * @author griffinbholt
 */
public final class FillService extends FillingService {
    private final AuthTokenDao authTokenDao;
    private final EventDao eventDao;
    private final PersonDao personDao;

    /**
     * Default constructor
     */
    public FillService() {
        this.authTokenDao = super.getAuthTokenDao();
        this.eventDao = super.getEventDao();
        this.personDao = super.getPersonDao();
    }

    // Methods
    /**
     * Populates the server's database with generated genealogical data for a specified user
     * @param request A {@link shared.request.FillRequest FillRequest} object that represents
     *                the request to fill the database with genealogical data
     * @return The result of the fill, represented as a {@link shared.result.FillResult FillResult} object
     */
    public FillResult fill(FillRequest request) {
        try {
            super.openConnection();
            FillResult success = process(request);
            super.commitChanges();
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            FillResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            super.closeConnection();
        }
    }

    private FillResult process(FillRequest request) throws DataAccessException {
        String username = request.getUserName();
        int numGenerations = request.getNumGenerations();
        checkNumGenerations(numGenerations);
        User user = this.userDao.getUserFromUsername(username);
        nullifyRelatives(user);
        eraseUserData(username);
        return generateFamilyData(user, numGenerations);
    }

    private void checkNumGenerations(int numGenerations) throws DataAccessException {
        if (0 > numGenerations) {
            String errorMessage = "The number of generations to fill must be non-negative.";
            throw new DataAccessException(errorMessage);
        }
    }

    private void nullifyRelatives(User user) {
        ServerPerson person = user.getPersonObj();
        person.setMotherID(null);
        person.setFatherID(null);
        person.setSpouseID(null);
    }

    private void eraseUserData(String username) throws DataAccessException {
        String userID = this.userDao.getUserIDFromUsername(username);
        this.userDao.eraseUserData(userID);
        this.personDao.eraseUserData(userID);
        this.eventDao.eraseUserData(userID);
        this.authTokenDao.eraseUserData(userID);
    }

    private FillResult generateFamilyData(User user, int numGenerations) throws DataAccessException {
        String userID = this.userDao.add(user);

        int numPersonsGenerated = super.generateAncestors(userID, user, numGenerations);
        int numEventsGenerated = super.generateEvents(user);

        return FillResult.newSuccess(numPersonsGenerated + 1, numEventsGenerated);
    }

    @Override
    protected FillResult reportError(Exception e) {
        String errorMessage = "Error: Fill request failed. " + e.getMessage();
        logError(errorMessage);
        return FillResult.newFailure(errorMessage);
    }
}
