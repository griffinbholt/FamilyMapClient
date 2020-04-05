package server.service;

import server.dao.GeneralDao;
import server.exception.DataAccessException;
import shared.result.Result;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class Service {
    @SuppressWarnings("PackageVisibleField")
    Connection dbConn;

    private final Logger logger;

    Service() {
        this.logger = Logger.getLogger(this.getClass().toString());
    }

    void openConnection() throws DataAccessException {
        try {
            dbConn = DriverManager.getConnection(GeneralDao.DB_CONNECTION_URL);

            dbConn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException("Unable to open connection to database");
        }
    }

    void commitChanges() {
        try {
            dbConn.commit();
        } catch (SQLException e) {
            logger.info("Unable to commit changes to the database");
            logger.info(e.getMessage());
        }
    }

    void rollbackChanges() {
        try {
            dbConn.rollback();
        } catch (SQLException e) {
            logger.info("Unable to rollback changes to the database");
            logger.info(e.getMessage());
        }
    }

    void closeConnection() {
        try {
            dbConn.close();
            dbConn = null;
        } catch (SQLException e) {
            logger.severe(e.getMessage());
            System.exit(0);
        }
    }

    protected abstract Result reportError(Exception e);

    void reportSuccess(Result result) {
        this.log(Level.INFO, result.getMessage());
    }

    private void log(Level level, String message) {
        logger.log(level, message + "\n");
    }

    void logError(String message) {
        this.log(Level.WARNING, message);
    }
}
