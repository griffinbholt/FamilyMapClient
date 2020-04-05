package server.exception;

/**
 * An exception thrown when a user enters an invalid password
 * @author griffinbholt
 */
public class InvalidPasswordException extends Exception {
    /**
     * The default error message for an <code>InvalidPasswordException</code>
     */
    public static final String MESSAGE = "Incorrect password.";

    /**
     * Default constructor
     */
    public InvalidPasswordException() {
        super(MESSAGE);
    }
}
