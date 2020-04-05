package shared.result;

/**
 * The result of an attempt to clear the database of all data.
 * Inherits from {@link Result Result}.
 * @author griffinbholt
 */
public final class ClearResult extends Result {
    private static final String SUCCESS = "Clear succeeded.";

    /**
     * Factory method that creates a new <code>ClearResult</code> object,
     * with <code>success</code> set to <code>true</code>
     * @return The new success <code>ClearResult</code> object
     */
    public static ClearResult newSuccess() {
        return new ClearResult(SUCCESS, true);
    }

    private ClearResult(String message, boolean success) {
        super(message, success);
    }

    /**
     * Factory method that creates a new <code>ClearResult</code> object,
     * with <code>success</code> set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed clear of the database
     * @return  The new failure <code>ClearResult</code> object
     */
    public static ClearResult newFailure(String errorMessage) {
        return new ClearResult(errorMessage, false);
    }
}
