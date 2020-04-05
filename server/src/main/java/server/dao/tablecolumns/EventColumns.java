package server.dao.tablecolumns;

/**
 * An enumeration for the names of columns in the "events" table in the database.
 * Columns (in order): ID, USER_ID, PERSON_ID, LATITUDE, LONGITUDE, COUNTRY, CITY, TYPE, YEAR
 * @author griffinbholt
 */
public enum EventColumns implements Columns {
    ID, USER_ID, PERSON_ID, LATITUDE, LONGITUDE, COUNTRY, CITY, TYPE, YEAR;

    /**
     * Returns the name of the enumerated "events" table column in all lowercase letters
     * @return The name of the enumerated "events" table column in all lowercase letters
     */
    @Override
    public String toLowerCase() {
            return this.toString().toLowerCase();
    }

    /**
     * Returns the SQL index of the enumerated "events" table column (SQL indices start at 1, instead of 0)
     * @return The SQL index of the enumerated "events" table column
     */
    @Override
    public int colIndex() {
        return this.ordinal() + 1;
    }
}
