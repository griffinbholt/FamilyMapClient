package server.service;

import server.exception.DataAccessException;
import shared.model.ServerEvent;
import shared.model.ServerPerson;
import shared.model.User;
import shared.request.LoadRequest;
import shared.result.LoadResult;
import server.service.helper.DatabaseClearer;

import java.util.List;

/**
 * A <code>LoadService</code> object manages load requests
 * @author griffinbholt
 */
public final class LoadService extends LoadingService {
    private final DatabaseClearer databaseClearer;

    /**
     * Default constructor
     */
    public LoadService() {
        this.databaseClearer = new DatabaseClearer(this.userDao, this.getEventDao());
    }

    /**
     * Processes a request to clear all data from the database, and then loads the posted user, person, and event data
     * (found in the {@link shared.request.LoadRequest LoadRequest} object) into the database.
     * @param request The @link shared.requests.LoadRequest LoadRequest} object containing the
     *                posted user, person, and event data
     * @return The result of the request, in the form of a {@link shared.result.LoadResult LoadResult} object
     */
    public LoadResult load(LoadRequest request) {
        try {
            super.openConnection();
            LoadResult success = processLoadRequest(request);
            super.commitChanges();
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            LoadResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure ;
        } finally {
            super.closeConnection();
        }
    }

    private LoadResult processLoadRequest(LoadRequest request) throws DataAccessException {
        databaseClearer.clearAllTables();

        int numUsers = loadUsers(request);
        int numPersonsAdded = loadPersons(request);
        int numEventsAdded = loadRequestEvents(request);

        return LoadResult.newSuccess(numUsers, numPersonsAdded, numEventsAdded);
    }

    private int loadUsers(LoadRequest request) throws DataAccessException {
        List<User> users = request.getUsers();

        return this.userDao.add(users);
    }

    private int loadPersons(LoadRequest request) throws DataAccessException {
        List<ServerPerson> persons = request.getPersons();

        return loadPersons(persons);
    }


    private int loadRequestEvents(LoadRequest request) throws DataAccessException {
        List<ServerEvent> events = request.getEvents();

        return loadEvents(events);
    }

    @Override
    protected LoadResult reportError(Exception e) {
        String errorMessage = "Error: Load request failed. " + e.getMessage();
        logError(errorMessage);
        return LoadResult.newFailure(errorMessage);
    }
}
