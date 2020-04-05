package server.dao.tablecolumns;

/**
 * An enumeration for the names of columns in the "persons" table in the database.
 * Columns (in order): ID, USERNAME, PASSWORD, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, GENDER, PERSON_ID
 * @author griffinbholt
 */
public enum UserColumns implements Columns {
    ID, USERNAME, PASSWORD, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, GENDER, PERSON_ID;

    /**
     * Returns the name of the enumerated "users" table column in all lowercase letters
     * @return The name of the enumerated "users" table column in all lowercase letters
     */
    @Override
    public String toLowerCase() {
        return this.toString().toLowerCase();
    }

    /**
     * Returns the SQL index of the enumerated "users" table column (SQL indices start at 1, instead of 0)
     * @return The SQL index of the enumerated "users" table column
     */
    @Override
    public int colIndex() {
        return this.ordinal() + 1;
    }
}