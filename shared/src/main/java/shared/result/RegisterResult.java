package shared.result;

import shared.model.AuthToken;

/**
 * The result of a register request.
 * Inherits from {@link InfoResult InfoResult}.
 * @author griffinbholt
 */
public final class RegisterResult extends InfoResult {
    private static final String SUCCESS_VERB = "registered";

    /**
     * Factory method that creates a new <code>RegisterResult</code> object,
     * with <code>success</code> set to <code>true</code>
     * @param authToken Resulting {@link shared.model.AuthToken AuthToken}
     *                  for the user that was successfully registered
     * @param userName Username of the user that was successfully registered
     * @param personID Person ID of the user that was successfully registered
     * @return The new success <code>RegisterResult</code> object
     */
    public static RegisterResult newSuccess(AuthToken authToken, String userName, String personID) {
        return new RegisterResult(authToken, userName, personID);
    }

    private RegisterResult(AuthToken authToken, String userName, String personID) {
        super(authToken, userName, personID, SUCCESS_VERB);
    }

    /**
     * Factory method that creates a new <code>FillResult</code> object,
     * with <code>success</code> set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed registration of the user
     * @return The new failure <code>RegisterResult</code> object
     */
    public static RegisterResult newFailure(String errorMessage) {
        return new RegisterResult(errorMessage);
    }

    private RegisterResult(String errorMessage) {
        super(errorMessage);
    }
}
