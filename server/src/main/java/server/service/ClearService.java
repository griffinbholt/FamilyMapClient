package server.service;

import server.exception.DataAccessException;
import server.service.helper.DatabaseClearer;
import shared.result.ClearResult;

/**
 * A <code>ClearService</code> object manages the functionality to clear the database
 * @author griffinbholt
 */
public final class ClearService extends UsersService {
    private final DatabaseClearer databaseClearer;

    /**
     * Default constructor
     */
    public ClearService() {
        this.databaseClearer = new DatabaseClearer(this.userDao);
    }

    /**
     * Deletes ALL data from the database, including user accounts, auth tokens, and generated person and event data.
     * @return The result of the database clear, represented as a {@link shared.result.ClearResult ClearResult}
     *         object
     */
    public ClearResult clearTables() {
        try {
            this.openConnection();
            databaseClearer.clearAllTables();
            super.commitChanges();
            ClearResult success = ClearResult.newSuccess();
            super.reportSuccess(success);
            return success;
        } catch (DataAccessException e) {
            ClearResult failure = this.reportError(e);
            super.rollbackChanges();
            return failure;
        } finally {
            this.closeConnection();
        }
    }

    @Override
    void openConnection() throws DataAccessException {
        super.openConnection();
        this.databaseClearer.setConnection(dbConn);
    }

    @Override
    void closeConnection() {
        super.closeConnection();
        this.databaseClearer.setConnection(null);
    }

    @Override
    protected ClearResult reportError(Exception e) {
        String errorMessage = "Error: Clear request failed. " + e.getMessage();
        logError( errorMessage);
        return ClearResult.newFailure(errorMessage);
    }
}
