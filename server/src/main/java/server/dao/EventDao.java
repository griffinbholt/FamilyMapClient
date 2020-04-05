package server.dao;

import server.dao.tablecolumns.EventColumns;
import server.exception.DataAccessException;
import server.exception.InvalidAuthTokenException;
import shared.model.AuthToken;
import shared.model.ServerEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database access object for {@link ServerEvent ServerEvent} objects
 * @author griffinbholt
 */
public final class EventDao extends RequiringAuthorizationDao {
    /**
     * Creates a new <code>EventDao</code> object with the input {@link java.sql.Connection Connection} object and
     * {@link server.dao.UserDao UserDao} object
     * @param conn The {@link java.sql.Connection Connection} object, connecting to the database
     * @param userDao A {@link server.dao.UserDao UserDao} object to connect to the users table
     *                when managing events
     * @param authTokenDao A {@link server.dao.AuthTokenDao AuthTokenDao} object to connect to the
     *                    authorization token table when managing events
     */
    public EventDao(Connection conn, UserDao userDao, AuthTokenDao authTokenDao){
        super(conn, userDao, authTokenDao, "event", new String[]{
                "id",
                "user_id",
                "person_id",
                "latitude",
                "longitude",
                "country",
                "city",
                "type",
                "year"
        });
    }

    /**
     * Creates a new <code>EventDao</code> object with the input {@link java.sql.Connection Connection} object and
     * {@link server.dao.UserDao UserDao} object
     * @param userDao A {@link server.dao.UserDao UserDao} object to connect to the users table
     *                when managing events
     * @param authTokenDao A {@link server.dao.AuthTokenDao AuthTokenDao} object to connect to the
     *                    authorization token table when managing events
     */
    public EventDao(UserDao userDao, AuthTokenDao authTokenDao) {
        this(null, userDao, authTokenDao);
    }

    /**
     * Adds a single event into the database
     * @param event An {@link ServerEvent ServerEvent} object, whose information will be added
     *              to the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public void add(ServerEvent event) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            setInsertTableColumns(event, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_INSERTING);
        }
    }

    private void setInsertTableColumns(ServerEvent event, PreparedStatement stmt) throws SQLException, DataAccessException {
        String userID = getUserIDFromUsername(event.getAssociatedUsername());

        stmt.setString(EventColumns.ID.colIndex(), event.getEventID());
        stmt.setString(EventColumns.USER_ID.colIndex(), userID);
        stmt.setString(EventColumns.PERSON_ID.colIndex(), event.getPersonID());
        stmt.setDouble(EventColumns.LATITUDE.colIndex(), event.getLatitude());
        stmt.setDouble(EventColumns.LONGITUDE.colIndex(), event.getLongitude());
        stmt.setString(EventColumns.COUNTRY.colIndex(), event.getCountry());
        stmt.setString(EventColumns.CITY.colIndex(), event.getCity());
        stmt.setString(EventColumns.TYPE.colIndex(), event.getTypeName());
        stmt.setInt(EventColumns.YEAR.colIndex(), event.getYear().getValue());
    }

    /**
     * Adds a list of events into the database
     * @param events A list of {@link ServerEvent event} objects, whose information will be added
     *               to the database
     * @return The number of events added
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    @SuppressWarnings("BoundedWildcard")
    public int add(Iterable<ServerEvent> events) throws DataAccessException  {
        int numAdded = 0;

        for (ServerEvent event : events) {
            add(event);
            numAdded++;
        }

        return numAdded;
    }

    /**
     * Queries the database for the event associated with the input event ID.
     * Requires an {@link shared.model.AuthToken authToken} that is associated with the user to whom the event
     * belongs.
     * @param eventID Input event ID
     * @param authToken The required {@link shared.model.AuthToken authToken}
     * @return The {@link ServerEvent ServerEvent} associated with the input event ID; null, if no event
     * exists with the associated event ID, or if the input authorization token is invalid
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public ServerEvent getAuthorizedEvent(String eventID, AuthToken authToken) throws DataAccessException {
        String userID = getUserIDFromAuthToken(authToken);
        String username = getUsernameFromUserID(userID);

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_MATCHING_ID_AND_USER_ID)) {
            executeQuery(stmt, eventID, userID);
            return getAuthorizedEventQueryResult(eventID, username);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } catch (InvalidAuthTokenException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            closeResultSet();
        }
    }

    private ServerEvent getAuthorizedEventQueryResult(String eventID, String username)
            throws SQLException, InvalidAuthTokenException {
        if (queryFound()) {
            return createResultingEvent(eventID, username);
        }

        throw new InvalidAuthTokenException(NONE_EXIST_FOR_AUTH_TOKEN);
    }

    /**
     * Queries the database for the event with the input event ID, without requiring an authToken.
     * @param eventID The ID for the event being queried
     * @return The {@link ServerEvent event} object representing the event with the input ID;
     *         null, if no event found
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public ServerEvent getUnauthorizedEvent(String eventID) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(allEventsMatchingEventIDSQL())) {
            executeQuery(stmt, eventID);
            return getUnauthorizedEventQueryResult(eventID);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } finally {
            closeResultSet();
        }
    }

    private ServerEvent getUnauthorizedEventQueryResult(String eventID) throws SQLException, DataAccessException {
        if (queryFound()) {
            String userID = rs.getString(EventColumns.USER_ID.toLowerCase());
            String username = getUsernameFromUserID(userID);
            return createResultingEvent(eventID, username);
        } else {
            throw new DataAccessException(NONE_EXIST_FOR_ID + eventID);
        }
    }

    private String allEventsMatchingEventIDSQL() {
        return getAllColumnsQueryStatement(EventColumns.ID.ordinal());
    }

    /**
     * Queries the database for all events connected to the user, to whom the input authorization token is linked
     * @param authToken Input {@link shared.model.AuthToken AuthToken} for the user
     * @return A list of {@link ServerEvent event} objects, representing events connected to the user;
     *         an empty list, if no events are connected to the user; null, if the authorization token is invalid
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public List<ServerEvent> getAllConnectedEvents(AuthToken authToken) throws DataAccessException {
        String userID = getUserIDFromAuthToken(authToken);
        String username = getUsernameFromUserID(userID);

        try (PreparedStatement stmt = conn.prepareStatement(allEventsMatchingUserIDSQL())) {
            executeQuery(stmt, userID);
            return getAllEventsQueryResult(username);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } finally {
            closeResultSet();
        }
    }

    private String allEventsMatchingUserIDSQL() {
        return getAllColumnsQueryStatement(EventColumns.USER_ID.ordinal());
    }

    private List<ServerEvent> getAllEventsQueryResult(String username) throws SQLException {
        List<ServerEvent> events = new ArrayList<>();

        while (queryFound()) {
            events.add(createResultingEvent(username));
        }

        return events;
    }

    private ServerEvent createResultingEvent(String username) throws SQLException {
        String eventID = rs.getString(EventColumns.ID.toLowerCase());
        return createResultingEvent(eventID, username);
    }

    private ServerEvent createResultingEvent(String eventID, String username) throws SQLException {
        return new ServerEvent(eventID,
                         username,
                         rs.getString(EventColumns.PERSON_ID.toLowerCase()),
                         rs.getFloat(EventColumns.LATITUDE.toLowerCase()),
                         rs.getFloat(EventColumns.LONGITUDE.toLowerCase()),
                         rs.getString(EventColumns.COUNTRY.toLowerCase()),
                         rs.getString(EventColumns.CITY.toLowerCase()),
                         rs.getString(EventColumns.TYPE.toLowerCase()),
                         rs.getInt(EventColumns.YEAR.toLowerCase()));
    }
}
