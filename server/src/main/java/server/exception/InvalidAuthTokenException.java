package server.exception;

/**
 * An exception thrown when an authorization token cannot be found
 * @author griffinbholt
 */
public class InvalidAuthTokenException extends Exception {
    private static final String DEFAULT_MESSAGE = "Invalid authorization token.";
    /**
     * Constructor with a specified error message
     * @param message The error message
     */
    public InvalidAuthTokenException(String message)
    {
        super(message);
    }

    /**
     * Default constructor
     */
    public InvalidAuthTokenException() { super(DEFAULT_MESSAGE); }
}
