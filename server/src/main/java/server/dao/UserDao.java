package server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import server.dao.tablecolumns.UserColumns;
import server.exception.DataAccessException;
import server.exception.InvalidPasswordException;
import server.exception.UserNotFoundException;
import shared.model.ServerPerson;
import shared.model.User;
import shared.model.UserDBWrapper;

/**
 * Database access object for {@link shared.model.User User} objects
 * @author griffinbholt
 */
public final class UserDao extends GeneralDao {
    private static final String ERROR_QUERYING_USERNAME = "Error encountered while querying for username.";
    private static final String ERROR_CHECKING_PASSWORD = "Error encountered while checking password.";
    private static final String ERROR_QUERYING_USER_ID = "Error encountered while finding searching for userID " +
                                                         "associated with username: ";

    /**
     * A {@link server.dao.PersonDao PersonDao} object to connect to the persons table when managing persons
     */
    private PersonDao personDao;

    /**
     * Creates a new <code>UserDao</code> object with the input {@link java.sql.Connection Connection} object
     * @param conn The {@link java.sql.Connection Connection} object, connecting to the database
     */
    public UserDao(Connection conn) {
        super(conn, "user", new String[]{
                "id",
                "username",
                "password",
                "email_address",
                "first_name",
                "last_name",
                "gender",
                "person_id"
        });
    }

    /**
     * Creates a new <code>UserDao</code> object with the input {@link java.sql.Connection Connection} object
     */
    public UserDao() {
        this(null);
    }

    /**
     * Adds the input user into the database
     * @param user The {@link shared.model.User User} object representing the user and his/her information
     * @return The userID for the input user
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public String add(User user) throws DataAccessException {
        String userID = generateUserID(user);

        try (PreparedStatement stmt = conn.prepareStatement(this.INSERT_SQL)) {
            setInsertTableColumns(userID, user, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_INSERTING);
        }

        addUserPersonIfNotNull(user.getPersonObj());

        return userID;
    }

    private void addUserPersonIfNotNull(ServerPerson userPerson) throws DataAccessException {
        if (null != userPerson) {
            personDao.add(userPerson);
        }
    }


    private void setInsertTableColumns(String userID, User user, PreparedStatement stmt) throws SQLException {
        stmt.setString(UserColumns.ID.colIndex(), userID);
        stmt.setString(UserColumns.USERNAME.colIndex(), user.getUserName());
        stmt.setString(UserColumns.PASSWORD.colIndex(), user.getPassword());
        stmt.setString(UserColumns.EMAIL_ADDRESS.colIndex(), user.getEmail());
        stmt.setString(UserColumns.FIRST_NAME.colIndex(), user.getFirstName());
        stmt.setString(UserColumns.LAST_NAME.colIndex(), user.getLastName());
        stmt.setString(UserColumns.GENDER.colIndex(), user.getGenderAbbr());
        stmt.setString(UserColumns.PERSON_ID.colIndex(), user.getPersonID());
    }

    private String generateUserID(User user) {
        return Integer.toHexString(user.getUserName().hashCode());
    }

    /**
     * Adds the list of users into the database.
     * @param users The list of {@link shared.model.User User} objects to be added to the database
     * @return The number of users successfully added
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public int add(Iterable<User> users) throws DataAccessException {
        int numAdded = 0;

        for (User user : users) {
            add(user);
            numAdded++;
        }

        return numAdded;
    }

    /**
     * Checks the password against the database to see if it matches the input username.
     * @param username The username of the user trying to login
     * @param password The password of the user trying to login
     * @return The userID of the user and the {@link shared.model.User User} object
     *         in the form of a {@link shared.model.UserDBWrapper UserDBWrapper} wrapper object,
     *         if the password matches; otherwise, throws error
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public UserDBWrapper checkPassword(String username, String password) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(userMatchingUsernameSQL())) {
            executeQuery(stmt, username);
            UserDBWrapper userDBWrapper = getUserDBWrapperQueryResult(username);
            checkIfPasswordMatches(password, userDBWrapper.getUser());
            return userDBWrapper;
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_CHECKING_PASSWORD);
        } catch (UserNotFoundException | InvalidPasswordException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            closeResultSet();
        }
    }

    private UserDBWrapper getUserDBWrapperQueryResult(String username) throws SQLException, DataAccessException,
            UserNotFoundException {
        if (queryFound()) {
            String userID = rs.getString(UserColumns.ID.toLowerCase());

            User user = createResultingUser(username);

            return new UserDBWrapper(userID, user);
        }

        throw new UserNotFoundException();
    }

    private User getUserQueryResult(String username) throws SQLException, UserNotFoundException, DataAccessException {
        if (queryFound()) {
            return createResultingUser(username);
        }

        throw new UserNotFoundException();
    }

    private User createResultingUser(String username) throws SQLException, DataAccessException {
        String personID = rs.getString(UserColumns.PERSON_ID.toLowerCase());
        String password = rs.getString(UserColumns.PASSWORD.toLowerCase());
        String emailAddress = rs.getString(UserColumns.EMAIL_ADDRESS.toLowerCase());
        String firstName = rs.getString(UserColumns.FIRST_NAME.toLowerCase());
        String lastName = rs.getString(UserColumns.LAST_NAME.toLowerCase());
        String genderAbbrev = rs.getString(UserColumns.GENDER.toLowerCase());
        ServerPerson person = this.personDao.getUnauthorizedPerson(personID);

        return new User(username, password, emailAddress, firstName, lastName, genderAbbrev, personID, person);
    }


    private void checkIfPasswordMatches(String inputPassword, User user) throws InvalidPasswordException {
        if (!passwordMatches(inputPassword, user)) {
            throw new InvalidPasswordException();
        }
    }

    private String userMatchingUsernameSQL() {
        return getAllColumnsQueryStatement(UserColumns.USERNAME.ordinal());
    }

    private boolean passwordMatches(String enteredPassword, User user) {
        return enteredPassword.equals(user.getPassword());
    }

    /**
     * Queries the database for the information of the user with the given username
     * @param username Username of the user, for which information is being queried
     * @return The information of the user with the given username in the form of a
     *         {@link shared.model.User User} object; null, if no user with
     *         that username exists in the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public User getUserFromUsername(String username) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(userMatchingUsernameSQL())) {
            executeQuery(stmt, username);
            return getUserQueryResult(username);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING);
        } catch (UserNotFoundException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            closeResultSet();
        }
    }

    /**
     * Queries the database for the information of the user with the given userID. Only used in unit tests.
     * @param userID userID of the user, for which information is being queried
     * @return The information of the user with the given userID in the form of a
     *         {@link shared.model.User User} object; null, if no user with
     *         that userID exists in the database
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public String getUsernameFromUserID(String userID) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(usernameMatchingUserIDSQL())) {
            executeQuery(stmt, userID);
            return queryFound() ? getUsernameQueryResult() : null;
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING_USERNAME);
        } finally {
           closeResultSet();
        }
    }

    private String usernameMatchingUserIDSQL() {
        return super.getColumnQueryStatement(UserColumns.USERNAME.ordinal(), UserColumns.ID.ordinal());
    }

    private String getUsernameQueryResult() throws SQLException {
        return rs.getString(UserColumns.USERNAME.toLowerCase());
    }

    /**
     * Queries the database for the userID of the user with the given username
     * @param username Username of the user, for which the userID is being queried
     * @return The userID of the user
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public String getUserIDFromUsername(String username) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(userIDMatchingUsernameSQL())) {
            executeQuery(stmt, username);
            return queryFound() ? getUserIDQueryResult() : null;
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_QUERYING_USER_ID + username);
        } finally {
            closeResultSet();
        }
    }


    private String userIDMatchingUsernameSQL() {
        return super.getColumnQueryStatement(UserColumns.ID.ordinal(), UserColumns.USERNAME.ordinal());
    }

    private String getUserIDQueryResult() throws SQLException {
        return rs.getString(UserColumns.ID.toLowerCase());
    }

    // Getters
    public PersonDao getPersonDao() {
        return personDao;
    }

    // Setters
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public void setConnection(Connection conn) {
        super.setConnection(conn);

        if (conn != this.personDao.getConnection()) {
            this.personDao.setConnection(conn);
        }
    }
}
