package shared.result;

import shared.model.AuthToken;

/**
 * A superclass that represents the {@link shared.model.AuthToken AuthToken}, username,
 * and person ID resulting from a data request.
 * Inherits from {@link Result Result}.
 * Never used outside of its subclasses.
 * @author griffinbholt
 */
public abstract class InfoResult extends Result {
    /**
     * {@link shared.model.AuthToken AuthToken} generated as a result of the request
     */
    private final String authToken;

    /**
     * Username of the user related to the request
     */
    private final String userName;

    /**
     * Person ID of the person related to the request
     */
    private final String personID;

    InfoResult(AuthToken authToken, String userName, String personID, String successVerb) {
        this(authToken, userName, personID, "Successfully " + successVerb + " user: {" + userName + "}.", true);
    }

    public InfoResult(String errorMessage) {
        this(null, null, null, errorMessage, false);
    }

    private InfoResult(AuthToken authToken, String userName, String personID, String message, boolean success) {
        super(message, success);
        this.authToken = (null == authToken) ? null : authToken.toString();
        this.userName = userName;
        this.personID = personID;
    }

    // Getters
    public String getAuthTokenString() {
        return authToken;
    }

    public AuthToken getAuthToken() {
        return new AuthToken(authToken);
    }

    public String getUserName() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }
}
