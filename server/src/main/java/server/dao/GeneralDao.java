package server.dao;

import server.dao.tablecolumns.PersonColumns;
import server.dao.tablecolumns.UserColumns;
import server.exception.DataAccessException;

import java.sql.*;

/**
 * An abstract superclass for Database Access Objects.
 * @author griffinbholt
 */
@SuppressWarnings({"ConstantDeclaredInAbstractClass", "ClassWithTooManyFields"})
public abstract class GeneralDao {
    /**
     * The name of the database file
     */
    static final String DATABASE_FILE = "family_map.sqlite";

    /**
     * The name of the directory, in which the database resides
     */
    static final String DATABASE_FOLDER = "db";

    /**
     * The JDBC connection url to the SQL database
     */
    public static final String DB_CONNECTION_URL = "jdbc:sqlite:" + DATABASE_FOLDER + "/" + DATABASE_FILE;

    Connection conn;
    final String INSERT_SQL;

    ResultSet rs;

    private final String TABLE_NAME;
    private final String[] TABLE_COLUMNS;
    private final String SELECT_ALL_SQL;
    private final String ERASE_ALL_DATA_SQL;
    private final String ERASE_USER_DATA_SQL;
    private final String ERROR_DELETING;
    private final String ERROR_CHECKING_EMPTY;
    final String ERROR_INSERTING;
    final String ERROR_QUERYING;

    GeneralDao(Connection conn, String modelName, String[] tableColumns) {
        this.conn = conn;
        this.TABLE_NAME = modelName + "s";
        this.TABLE_COLUMNS = tableColumns;
        this.INSERT_SQL = generateInsertSQLStatement();
        this.SELECT_ALL_SQL = "SELECT * FROM " + TABLE_NAME;
        this.ERASE_ALL_DATA_SQL = "DELETE FROM " + TABLE_NAME;
        this.ERASE_USER_DATA_SQL = eraseUserDataInTableSQL();
        this.ERROR_DELETING = "Error encountered while deleting data in the " + TABLE_NAME + " table";
        this.ERROR_CHECKING_EMPTY = "Error encountered while checking if the " + TABLE_NAME + " table is empty";
        this.ERROR_INSERTING = "Error encountered while inserting " + modelName + " into the database.";
        this.ERROR_QUERYING = "Error encountered while querying for " + modelName + ".";
    }

    private String generateInsertSQLStatement() {
        StringBuilder statement = new StringBuilder("INSERT INTO ").append(TABLE_NAME).append(" (");

        int LAST_INDEX = TABLE_COLUMNS.length - 1;

        appendColumnNames(LAST_INDEX, statement);
        statement.append(") VALUES(");
        appendPlaceholders(LAST_INDEX, statement);
        statement.append(")");

        return statement.toString();
    }

    private void appendColumnNames(int lastIndex, StringBuilder statement) {
        for (int i = 0; i <= lastIndex; i++) {
            statement.append(TABLE_COLUMNS[i]);

            if (i != lastIndex) {
                statement.append(", ");
            }
        }
    }

    private void appendPlaceholders(int lastIndex, StringBuilder statement) {
        for (int i = 0; i <= lastIndex; i++) {
            statement.append("?");

            if (i != lastIndex) {
                statement.append(",");
            }
        }
    }

    private String eraseUserDataInTableSQL() {
        String user_id = getIDString();
        return "DELETE FROM " + TABLE_NAME + " WHERE " + user_id + "= ?;";
    }

    private String getIDString() {
        if ("users".equals(TABLE_NAME)) {
            return UserColumns.ID.toLowerCase();
        }

        return PersonColumns.USER_ID.toLowerCase();
    }

    /**
     * Deletes all items from the specified table
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public void deleteAll() throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(ERASE_ALL_DATA_SQL)) {
            this.executeUpdate(stmt);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_DELETING);
        }
    }

    /**
     * Deletes all items from the specific table belonging to the user with the input userID
     * @param userID Input userID
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public void eraseUserData(String userID) throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(ERASE_USER_DATA_SQL)) {
            this.executeUpdate(stmt, userID);
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_DELETING);
        }
    }

    /**
     * Checks if the database table associated with the DAO is empty. Used only for testing purposes.
     * @return true, if the table is empty; false, otherwise
     * @throws DataAccessException An error that occurs when attempting to access the database
     */
    public boolean isTableEmpty() throws DataAccessException {
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL)) {
            this.executeQuery(stmt);
            return !queryFound();
        } catch (SQLException e) {
            throw new DataAccessException(ERROR_CHECKING_EMPTY);
        } finally {
            closeResultSet();
        }
    }

    void executeQuery(PreparedStatement stmt, String... parameters) throws SQLException {
        setParameters(stmt, parameters);
        rs = stmt.executeQuery();
    }

    private void executeUpdate(PreparedStatement stmt, String... parameters) throws SQLException {
        setParameters(stmt, parameters);
        stmt.executeUpdate();
    }

    private void setParameters(PreparedStatement stmt, String... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            stmt.setString(i + 1, parameters[i]);
        }
    }

    boolean queryFound() throws SQLException {
        return rs.next();
    }

    String getColumnQueryStatement(int selectColumn, int whereColumn) {
        return "SELECT " + TABLE_COLUMNS[selectColumn] + " FROM " + TABLE_NAME + " WHERE "
                + TABLE_COLUMNS[whereColumn] + "= ?;";
    }

    String getAllColumnsQueryStatement(int whereColumn) {
        return "SELECT * FROM " + TABLE_NAME + " WHERE " + TABLE_COLUMNS[whereColumn] + "= ?;";
    }

    void closeResultSet() throws DataAccessException {
        if (null != rs) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    // Getter
    Connection getConnection() {
        return conn;
    }

    // Setter
    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
