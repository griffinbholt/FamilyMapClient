package shared.model;

import java.util.Objects;

/**
 * A Plain Old Java Object to represent authorization tokens utilized by the server
 * @author griffinbholt
 */
public class AuthToken {
    /**
     * A unique string used as an authorization token
     */
    private final String tokenString;

    /**
     * Constructs an <code>AuthToken</code> object with the input token string
     * @param authTokenString The authorization token string
     */
    public AuthToken(String authTokenString) {
        this.tokenString = authTokenString;
    }

    /**
     * Returns the authorization token string
     * @return The authorization token string
     */
    @Override
    public String toString() {
        return this.tokenString;
    }

    /**
     * Tests if the input <code>AuthToken</code> is equal to the current instance
     * @param o Input <code>Object</code> to be tested for equality with the current <code>AuthToken</code> instance
     * @return true, if the two authorization tokens are the same; false, if otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthToken)) return false;
        AuthToken authToken = (AuthToken) o;
        return tokenString.equals(authToken.tokenString);
    }

    /**
     * Generates the hashcode of the authorization token
     * @return The hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(tokenString);
    }
}

