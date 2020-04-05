package shared.model;

/**
 * A wrapper class that contains both a {@link User} object and its userID extracted from the database
 */
public class UserDBWrapper {
    private final String userID;
    private final User user;

    /**
     * Constructor
     * @param userID The userID, extracted from the database, of the user
     * @param user The {@link User} object, representing the user
     */
    public UserDBWrapper(String userID, User user) {
        this.userID = userID;
        this.user = user;
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public User getUser() {
        return user;
    }
}
