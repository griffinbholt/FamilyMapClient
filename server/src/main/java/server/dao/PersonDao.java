package server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import server.dao.tablecolumns.EventColumns;
import server.dao.tablecolumns.PersonColumns;
import server.exception.DataAccessException;
import server.exception.InvalidAuthTokenException;
import shared.model.AuthToken;
import shared.model.ServerPerson;

/**
 * Database access object for {@link ServerPerson Person} objects
 * @author griffinbholt
 */
public final class PersonDao extends RequiringAuthorizationDao {
    /**
     * Creates a new <code>PersonDao</code> object with the input {@link java.sql.Connection Connection} object and
     * {@link server.dao.UserDao UserDao} object
     * @param conn The {@link java.sql.Connection Connection} object, connecting to the database
     * @param userDao A {@link server.dao.UserDao UserDao} object to connect to the users table
     *                when managing persons
     * @param authTokenDao A {@link server.dao.AuthTokenDao AuthTokenDao} object to connect to the
     *                    auth_tokens table when managing persons
     */
    public PersonDao(Connection conn, UserDao userDao, AuthTokenDao authTokenDao) {
        super(conn, userDao, authTokenDao, "person", new String[]{
                "id",
                "user_id",
                "first_name",
                "last_name",
                "gender",
                "father_id",
                "mother_id",
                "spouse_id"
        });
    }

    /**
     * Creates a new <code>PersonDao</code> object with the input {@link java.sql.Connection Connection} object and
     * {@link server.dao.UserDao UserDao} object
     * @param userDao A {@link server.dao.UserDao UserDao} object to connect to the users table
     *                when managing persons
     * @param authTokenDao A {@link server.dao.AuthTokenDao AuthTokenDao} object to connect to the
     *                    auth_tokens table when managing persons
     */
    public PersonDao(UserDao userDao, AuthTokenDao authTokenDao) { this(null, userDao, authTokenDao); }

    /**
     * Adds a single {@link ServerPerson Person} object to the database
     * @param person The {@link ServerPerson Person} object
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public void add(ServerPerson person) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(this.INSERT_SQL)) {
            setInsertTableColumns(person, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_INSERTING);
        }
    }

    private void setInsertTableColumns(ServerPerson person, PreparedStatement stmt) throws DataAccessException, SQLException {
        String userID = getUserIDFromUsername(person.getAssociatedUsername());

        stmt.setString(PersonColumns.ID.colIndex(), person.getPersonID());
        stmt.setString(PersonColumns.USER_ID.colIndex(), userID);
        stmt.setString(PersonColumns.FIRST_NAME.colIndex(), person.getFirstName());
        stmt.setString(PersonColumns.LAST_NAME.colIndex(), person.getLastName());
        stmt.setString(PersonColumns.GENDER.colIndex(), person.getGenderAbbrev());
        stmt.setString(PersonColumns.FATHER_ID.colIndex(), person.getFatherID());
        stmt.setString(PersonColumns.MOTHER_ID.colIndex(), person.getMotherID());
        stmt.setString(PersonColumns.SPOUSE_ID.colIndex(), person.getSpouseID());
    }

    /**
     * Adds a list of persons to the database
     * @param persons A list of {@link ServerPerson Person} objects representing the ancestors to be
     *                added to the database
     * @return The number of persons added to the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public int add(Iterable<ServerPerson> persons) throws DataAccessException {
        int numAdded = 0;

        for (ServerPerson person : persons) {
            add(person);
            numAdded++;
        }

        return numAdded;
    }

    /**
     * Queries the database for the person associated with the input person ID.
     * Requires an {@link shared.model.AuthToken authToken} that is associated with the user to whom the person
     * belongs.
     * @param personID Input person ID
     * @param authToken The required {@link shared.model.AuthToken authToken}
     * @return The {@link ServerPerson Person} associated with the input person ID; null, if no person
     * exists with the associated person ID, or if the input authorization token
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public ServerPerson getAuthorizedPerson(String personID, AuthToken authToken) throws DataAccessException {
        String userID = getUserIDFromAuthToken(authToken);
        String username = getUsernameFromUserID(userID);

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_MATCHING_ID_AND_USER_ID)) {
            executeQuery(stmt, personID, userID);
            return getAuthorizedPersonQueryResult(personID, username);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } catch (InvalidAuthTokenException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            closeResultSet();
        }
    }

    private ServerPerson getAuthorizedPersonQueryResult(String personID, String username)
            throws SQLException, InvalidAuthTokenException {
        if (queryFound()) {
            return createResultingPerson(personID, username);
        }

        throw new InvalidAuthTokenException(NONE_EXIST_FOR_AUTH_TOKEN);
    }

    /**
     * Queries the database for the person associated with the input person ID, without requiring an authToken.
     * @param personID Input person ID
     * @return The {@link ServerPerson Person} associated with the input person ID; null, if no person
     *         exists with the associated person ID
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public ServerPerson getUnauthorizedPerson(String personID) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(allPersonsMatchingPersonIDSQL())) {
            executeQuery(stmt, personID);
            return getUnauthorizedPersonQueryResult(personID);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } finally {
           closeResultSet();
        }
    }

    private String allPersonsMatchingPersonIDSQL() {
        return super.getAllColumnsQueryStatement(PersonColumns.ID.ordinal());
    }

    private ServerPerson getUnauthorizedPersonQueryResult(String personID) throws SQLException, DataAccessException {
        if (queryFound()) {
            String userID = rs.getString(PersonColumns.USER_ID.toLowerCase());
            String username = getUsernameFromUserID(userID);
            return createResultingPerson(personID, username);
        }

        throw new DataAccessException(NONE_EXIST_FOR_ID + personID);
    }

    /**
     * Queries the database for all ancestors of the user linked to the input authorization token
     * @param authToken The {@link shared.model.AuthToken AuthToken} linked to the user
     * @return A list of {@link ServerPerson Person} objects representing all ancestors of the user;
     *         empty, if the user has no ancestors; null, if the authorization token is invalid
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public List<ServerPerson> getAncestorsFromAuthToken(AuthToken authToken) throws DataAccessException {
        String userID = getUserIDFromAuthToken(authToken);
        String username = getUsernameFromUserID(userID);

        try (PreparedStatement stmt = conn.prepareStatement(allPersonsMatchingUserIDSQL())) {
            executeQuery(stmt, userID);
            return getAncestorsQueryResult(username);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } finally {
            closeResultSet();
        }
    }

    private String allPersonsMatchingUserIDSQL() {
        return getAllColumnsQueryStatement(EventColumns.USER_ID.ordinal());
    }

    private List<ServerPerson> getAncestorsQueryResult(String username) throws SQLException {
        List<ServerPerson> persons = new ArrayList<>();

        while (queryFound()) {
            persons.add(createResultingPerson(username));
        }

        return persons;
    }

    private ServerPerson createResultingPerson(String username) throws SQLException {
        String personID = rs.getString(PersonColumns.ID.toLowerCase());
        return createResultingPerson(personID, username);
    }

    private ServerPerson createResultingPerson(String personID, String username) throws SQLException {
        return new ServerPerson(personID,
                          username,
                          rs.getString(PersonColumns.FIRST_NAME.toLowerCase()),
                          rs.getString(PersonColumns.LAST_NAME.toLowerCase()),
                          rs.getString(PersonColumns.GENDER.toLowerCase()),
                          rs.getString(PersonColumns.FATHER_ID.toLowerCase()),
                          rs.getString(PersonColumns.MOTHER_ID.toLowerCase()),
                          rs.getString(PersonColumns.SPOUSE_ID.toLowerCase()));
    }
}
