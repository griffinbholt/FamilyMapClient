package server.service.helper;

import java.sql.Connection;

import server.dao.AuthTokenDao;
import server.dao.EventDao;
import server.dao.PersonDao;
import server.dao.UserDao;
import server.exception.DataAccessException;

/**
 * A helper object that manages the functionality for clearing all data in the database
 * @author griffinbholt
 */
public class DatabaseClearer {
    private final UserDao userDao;
    private final AuthTokenDao authTokenDao;
    private final PersonDao personDao;
    private final EventDao eventDao;

    /**
     * Constructor that creates a previously non-existent {@link EventDao} object (required for database clears)
     * @param userDao A {@link UserDao} object (required for database clears)
     */
    public DatabaseClearer(UserDao userDao) {
        this.userDao = userDao;
        this.personDao = this.userDao.getPersonDao();
        this.authTokenDao = personDao.getAuthTokenDao();
        this.eventDao = new EventDao(this.userDao, this.authTokenDao);
    }

    /**
     * Constructor
     * @param userDao A {@link UserDao} object (required for database clears)
     * @param eventDao A {@link EventDao} object (required for database clears)
     */
    public DatabaseClearer(UserDao userDao, EventDao eventDao) {
        this.userDao = userDao;
        this.personDao = this.userDao.getPersonDao();
        this.authTokenDao = personDao.getAuthTokenDao();
        this.eventDao = eventDao;
    }

    /**
     * Sets the database connection for the {@link EventDao} object used to clear the database
     * @param conn The SQL database {@link Connection}
     */
    public void setConnection(Connection conn) {
        this.eventDao.setConnection(conn);
    }

    /**
     * Clears all tables in the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public void clearAllTables() throws DataAccessException {
        this.userDao.deleteAll();
        this.authTokenDao.deleteAll();
        this.personDao.deleteAll();
        this.eventDao.deleteAll();
    }
}
