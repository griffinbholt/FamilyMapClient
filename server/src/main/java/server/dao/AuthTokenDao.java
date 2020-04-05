package server.dao;

import server.dao.tablecolumns.AuthTokenColumns;
import server.exception.DataAccessException;
import server.exception.InvalidAuthTokenException;
import shared.model.AuthToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Database access object for {@link shared.model.AuthToken AuthToken} objects
 * @author griffinbholt
 */
public final class AuthTokenDao extends GeneralDao {
    /**
     * Creates a new <code>AuthTokenDao</code> object with the input {@link java.sql.Connection Connection} object
     * @param conn The {@link java.sql.Connection Connection} object, connecting to the database
     */
    public AuthTokenDao(Connection conn) {
        super(conn, "auth_token", new String[]{
                "user_id",
                "auth_token"
        });
    }

    /**
     * Creates a new <code>AuthTokenDao</code> object with the input {@link java.sql.Connection Connection} object
     */
    public AuthTokenDao() {
        this(null);
    }

    /**
     * Generates a unique authorization token for the given userID and adds it to the database
     * @param userID The userID of the user for which the authorization token is generated
     * @return The generated unique authorization token for the user
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public AuthToken generate(String userID) throws DataAccessException {
        AuthToken newAuthToken = generateValidAuthToken();
        addAuthTokenToDatabase(userID, newAuthToken);
        return newAuthToken;
    }

    private AuthToken generateValidAuthToken() throws DataAccessException {
        String sql = allColsOfAuthTokensTableSQL();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String checkedToken = generateTokenCheckedAgainstDB(stmt);
            return new AuthToken(checkedToken);
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while generating authorization token");
        } finally {
            super.closeResultSet();
        }
    }

    private String allColsOfAuthTokensTableSQL() {
        return super.getAllColumnsQueryStatement(AuthTokenColumns.AUTH_TOKEN.ordinal());
    }

    private String generateTokenCheckedAgainstDB(PreparedStatement stmt) throws SQLException {
        boolean tokenAlreadyExists = true;
        String uncheckedToken = null;

        while (tokenAlreadyExists) {
            uncheckedToken = generateRandomTokenStr();
            executeQuery(stmt, uncheckedToken);
            tokenAlreadyExists = super.queryFound();
        }

        return uncheckedToken;
    }

    private String generateRandomTokenStr() {
        return UUID.randomUUID().toString();
    }

    private void addAuthTokenToDatabase(String userID, AuthToken authToken) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(this.INSERT_SQL)) {
            setInsertTableColumns(userID, authToken, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_INSERTING);
        }
    }

    private void setInsertTableColumns(String userID, AuthToken authToken, PreparedStatement stmt) throws SQLException {
        stmt.setString(AuthTokenColumns.USER_ID.colIndex(), userID);
        stmt.setString(AuthTokenColumns.AUTH_TOKEN.colIndex(), authToken.toString());
    }

    /**
     * Queries the database for a user_id associated with the input authorization token
     * @param authToken Authorization token to be queried
     * @return The user_id associated with the input authorization token; null, if the authorization token is not valid
     * @throws DataAccessException An error that occurs when attempting to access the database
     * */
    public String getUserID(AuthToken authToken) throws DataAccessException {
        String sql = userIDMatchingAuthTokenSQL();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            return queryUserIDInDB(authToken, stmt);
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while querying userID.");
        } catch (InvalidAuthTokenException e) {
            throw new DataAccessException(e.getMessage());
        }
        finally {
            super.closeResultSet();
        }
    }

    private String userIDMatchingAuthTokenSQL() {
        int USER_ID_ORDINAL = AuthTokenColumns.USER_ID.ordinal();
        int AUTH_TOKEN_ORDINAL = AuthTokenColumns.AUTH_TOKEN.ordinal();
        return super.getColumnQueryStatement(USER_ID_ORDINAL, AUTH_TOKEN_ORDINAL);
    }

    private String queryUserIDInDB(AuthToken authToken, PreparedStatement stmt) throws SQLException,
            InvalidAuthTokenException {
        super.executeQuery(stmt, authToken.toString());

        if (super.queryFound()) {
            return getResultingUserID();
        } else {
            throw new InvalidAuthTokenException();
        }
    }

    private String getResultingUserID() throws SQLException {
        return rs.getString(AuthTokenColumns.USER_ID.toLowerCase());
    }
}
