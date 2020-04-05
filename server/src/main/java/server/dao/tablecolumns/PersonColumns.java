package server.dao.tablecolumns;

/**
 * An enumeration for the names of columns in the "persons" table in the database.
 * Columns (in order): ID, USER_ID, FIRST_NAME, LAST_NAME, GENDER, FATHER_ID, MOTHER_ID, SPOUSE_ID
 * @author griffinbholt
 */
public enum PersonColumns implements Columns {
    ID, USER_ID, FIRST_NAME, LAST_NAME, GENDER, FATHER_ID, MOTHER_ID, SPOUSE_ID;

    /**
     * Returns the name of the enumerated "persons" table column in all lowercase letters
     * @return The name of the enumerated "persons" table column in all lowercase letters
     */
    @Override
    public String toLowerCase() {
        return this.toString().toLowerCase();
    }

    /**
     * Returns the SQL index of the enumerated "persons" table column (SQL indices start at 1, instead of 0)
     * @return The SQL index of the enumerated "persons" table column
     */
    @Override
    public int colIndex() {
        return this.ordinal() + 1;
    }
}