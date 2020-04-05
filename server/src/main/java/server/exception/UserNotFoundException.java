package server.exception;

/**
 * An exception thrown when a user enters a username that does not exist in the database
 * @author griffinbholt
 */
public class UserNotFoundException extends Exception {
    /**
     * The default error message for an <code>UserNotFoundException</code>
     */
    public static final String MESSAGE = "That username does not exist in our records.";

    /**
     * Default constructor
     */
    public UserNotFoundException() {
        super(MESSAGE);
    }
}
