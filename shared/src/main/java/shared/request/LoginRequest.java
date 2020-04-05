package shared.request;

import java.util.Objects;

/**
 * A Plain Old Java Object representing a request to login a user via username and password
 * @author griffinbholt
 */
public class LoginRequest {
    /**
     * The username associated with the user's account
     */
    private final String userName;

    /**
     * The password associated with the user's account
     */
    private final String password;

    /**
     * Creates a new <code>LoginRequest</code> object with the instance variable values input through the parameters
     * @param userName Input username for the login request
     * @param password Input password for the login request
     */
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Generates a string representation of the login request
     * @return A string representation of the login request
     */
    @Override
    public String toString() {
        return "LoginRequest{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * Tests if the input <code>LoginRequest</code> object is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>LoginRequest</code> instance
     * @return true, if the two login requests are have the same username and password; false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginRequest)) return false;
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(getUserName(), that.getUserName()) &&
                Objects.equals(getPassword(), that.getPassword());
    }

    /**
     * Generates the hashcode for the login request
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getPassword());
    }

    // Getters
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
