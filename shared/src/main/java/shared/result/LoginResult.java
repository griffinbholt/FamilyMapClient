package shared.result;

import shared.model.AuthToken;

/**
 * The result of a login request.
 * Inherits from {@link InfoResult InfoResult}.
 * @author griffinbholt
 */
public final class LoginResult extends InfoResult {
    private static final String SUCCESS_VERB = "logged in";

    /**
     * Factory method that creates a new <code>LoginResult</code> object, with <code>success</code>
     * set to <code>true</code>
     * @param authToken Resulting {@link shared.model.AuthToken AuthToken} for the user that was
     *                  successfully logged in
     * @param userName Username of the user that was successfully logged in
     * @param personID Person ID of the user that was successfully logged in
     * @return A success <code>LoginResult</code> object
     */
    public static LoginResult newSuccess(AuthToken authToken, String userName, String personID) {
        return new LoginResult(authToken, userName, personID);
    }

    private LoginResult(AuthToken authToken, String userName, String personID) {
        super(authToken, userName, personID, SUCCESS_VERB);
    }

    /**
     * Factory method that creates a new <code>LoginResult</code> object, with <code>success</code>
     * set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed login
     * @return A failure <code>LoginResult</code> object
     */
    public static LoginResult newFailure(String errorMessage) {
        return new LoginResult(errorMessage);
    }

    private LoginResult(String errorMessage) {
        super(errorMessage);
    }
}
