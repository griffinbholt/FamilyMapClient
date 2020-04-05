package server.service.helper;

import java.sql.Connection;

import server.dao.AuthTokenDao;
import server.dao.EventDao;
import server.dao.PersonDao;
import server.exception.DataAccessException;
import shared.model.ServerEvent;
import shared.model.ServerPerson;

/**
 * A helper object that loads family data ({@link ServerPerson persons} and {@link ServerEvent events}) into the database
 * @author griffinbholt
 */
public class FamilyDataLoader {
    private final PersonDao personDao;
    private final EventDao eventDao;

    /**
     * Constructor
     * @param personDao A {@link PersonDao} object (required for loading {@link ServerPerson} objects into the database)
     * @param eventDao A {@link EventDao} object (required for loading {@link ServerEvent} objects into the database)
     */
    public FamilyDataLoader(PersonDao personDao, EventDao eventDao) {
        this.personDao = personDao;
        this.eventDao = eventDao;
    }

    /**
     * Loads the input collection of {@link ServerPerson} objects into the database
     * @param persons Collection of {@link ServerPerson} objects to be loaded into the database
     * @return The number of total persons added to the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public int loadPersons(Iterable<ServerPerson> persons) throws DataAccessException {
        return personDao.add(persons);
    }

    /**
     * Loads the {@link ServerPerson} object of the user with the input userID
     * @param userID userID of the user, to whom the {@link ServerPerson} object belongs
     * @param person The user's {@link ServerPerson} object
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public void loadUserPerson(String userID, ServerPerson person) throws DataAccessException {
        personDao.eraseUserData(userID);
        personDao.add(person);
    }

    /**
     * Loads the input collection of {@link ServerEvent} objects into the database
     * @param events Collection of {@link ServerEvent} objects to be loaded into the database
     * @return The number of total events added to the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public int loadEvents(Iterable<ServerEvent> events) throws DataAccessException {
        return eventDao.add(events);
    }

    /**
     * Sets the database connection for the {@link PersonDao} and {@link EventDao} objects
     * @param conn The SQL database {@link Connection}
     */
    public void setConnection(Connection conn) {
        this.personDao.setConnection(conn);
        this.eventDao.setConnection(conn);
    }

    // Getter
    public AuthTokenDao getAuthTokenDao() {
        return personDao.getAuthTokenDao();
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public EventDao getEventDao() {
        return eventDao;
    }
}
