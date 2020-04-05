package server.service.helper;

import server.dao.AuthTokenDao;
import server.dao.UserDao;
import server.exception.DataAccessException;
import shared.model.AuthToken;
import shared.model.UserDBWrapper;
import shared.result.LoginResult;

import java.sql.Connection;

/**
 * A helper object that manages the functionality for logging in a user
 * @author griffinbholt
 */
public class LoginManager {
    private final UserDao userDao;
    private final AuthTokenDao authTokenDao;

    /**
     * Constructor
     * @param userDao A {@link UserDao} object (required for log-ins)
     */
    public LoginManager(UserDao userDao) {
        this.userDao = userDao;
        this.authTokenDao = userDao.getPersonDao().getAuthTokenDao();
    }

    /**
     * Logs in the user with the input username and password, if they are valid.
     * @param username Input username
     * @param password Input password
     * @return A {@link LoginResult}, reporting the result of the login
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public LoginResult loginUserForLoginResult(String username, String password)
            throws DataAccessException {
        UserDBWrapper userDBWrapper = userDao.checkPassword(username, password);
        String userID = userDBWrapper.getUserID();
        String personID = userDBWrapper.getUser().getPersonID();

        AuthToken authToken = authTokenDao.generate(userID);

        return LoginResult.newSuccess(authToken, username, personID);
    }

    /**
     * Logs in the user with the input username and password, if they are valid.
     * @param username Input username
     * @param password Input password
     * @return An {@link AuthToken} generated for the now logged-in user
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public AuthToken loginUserForAuthToken(String username, String password) throws DataAccessException {
        String userID = userDao.checkPassword(username, password).getUserID();
        return authTokenDao.generate(userID);
    }

    /**
     * Sets the database connection for the database access objects used to log the user in
     * @param conn The SQL database {@link Connection}
     */
    public void setConnection(Connection conn) {
        this.authTokenDao.setConnection(conn);
    }
}
