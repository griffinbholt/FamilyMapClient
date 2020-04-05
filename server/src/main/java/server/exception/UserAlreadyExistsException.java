package server.exception;

/**
 * An error thrown when a user tries to register a username that already exists in the database
 * @author griffinbholt
 */
public class UserAlreadyExistsException extends Exception {
    private static final String MESSAGE = "That username has already been taken.";

    /**
     * Default constructor
     */
    public UserAlreadyExistsException() {
        super(MESSAGE);
    }
}
