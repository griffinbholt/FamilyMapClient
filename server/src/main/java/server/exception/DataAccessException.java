package server.exception;

/**
 * An exception thrown in the case of an error when attempting to access the database
 * @author griffinbholt
 */
public class DataAccessException extends Exception {
    /**
     * Constructor with a specified error message
     * @param message The error message
     */
    public DataAccessException(String message)
    {
        super(message);
    }
}
