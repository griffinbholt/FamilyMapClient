package server.dao;

import server.exception.DataAccessException;
import shared.model.AuthToken;

import java.sql.Connection;

/**
 * An abstract superclass for {@link server.dao.PersonDao PersonDao} and
 * {@link server.dao.EventDao EventDao} objects.
 */
abstract class RequiringAuthorizationDao extends GeneralDao {
    /**
     * A {@link server.dao.UserDao UserDao} object to connect to the users table when managing events
     */
    private final UserDao userDao;

    /**
     * A {@link server.dao.AuthTokenDao AuthTokenDao} object to connect to the authorization token table
     * when managing events
     */
    private final AuthTokenDao authTokenDao;

    final String SELECT_MATCHING_ID_AND_USER_ID;
    final String NONE_EXIST_FOR_AUTH_TOKEN;
    final String NONE_EXIST_FOR_ID;

    RequiringAuthorizationDao(Connection conn, UserDao userDao, AuthTokenDao authTokenDao, String modelName,
                               String[] tableColumns) {
        super(conn, modelName, tableColumns);

        this.SELECT_MATCHING_ID_AND_USER_ID = "SELECT * FROM " + modelName +
                "s WHERE " + modelName + "s.id = ? AND " + modelName + "s.user_id = ?";

        this.userDao = userDao;
        this.authTokenDao = authTokenDao;
        this.NONE_EXIST_FOR_AUTH_TOKEN = "No such " + modelName + " exists for that authorization token.";
        this.NONE_EXIST_FOR_ID = "No " + modelName + " exists in the database with " + modelName +"ID: ";
    }

    String getUserIDFromAuthToken(AuthToken authToken) throws DataAccessException {
        return authTokenDao.getUserID(authToken);
    }

    String getUserIDFromUsername(String username) throws DataAccessException {
        return userDao.getUserIDFromUsername(username);
    }

    String getUsernameFromUserID(String userID) throws DataAccessException {
        return userDao.getUsernameFromUserID(userID);
    }

    // Getter
    public AuthTokenDao getAuthTokenDao() {
        return authTokenDao;
    }

    // Setter
    @Override
    public void setConnection(Connection conn) {
        super.setConnection(conn);

        if (conn != this.userDao.getConnection()) {
            this.userDao.setConnection(conn);
        }

        if (conn != this.authTokenDao.getConnection()) {
            this.authTokenDao.setConnection(conn);
        }
    }
}
