package server.dao.tablecolumns;

/**
 * An enumeration for the names of columns in the "auth_tokens" table in the database.
 * Columns (in order): USER_ID, AUTH_TOKEN
 * @author griffinbholt
 */
public enum AuthTokenColumns implements Columns {
    USER_ID, AUTH_TOKEN;

    /**
     * Returns the name of the enumerated "auth_tokens" table column in all lowercase letters
     * @return The name of the enumerated "auth_tokens" table column in all lowercase letters
     */
    @Override
    public String toLowerCase() {
        return this.toString().toLowerCase();
    }

    /**
     * Returns the SQL index of the enumerated "auth_tokens" table column (SQL indices start at 1, instead of 0)
     * @return The SQL index of the enumerated "auth_tokens" table column
     */
    @Override
    public int colIndex() {
        return this.ordinal() + 1;
    }
}
