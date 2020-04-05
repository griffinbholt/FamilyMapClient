package shared.result;

/**
 * The result of a fill request.
 * Inherits from {@link Result Result}.
 * @author griffinbholt
 */
public final class FillResult extends Result {
    /**
     * Factory method that creates a new <code>FillResult</code> object, with <code>success</code>
     * set to <code>true</code>
     * @param numPersons The number of persons successfully added to the database
     * @param numEvents The number of events successfully added to the database
     * @return The new success <code>FillResult</code> object
     */
    public static FillResult newSuccess(int numPersons, int numEvents) {
        return new FillResult(numPersons, numEvents);
    }

    private FillResult(int numPersons, int numEvents) {
        super("Successfully added " + numPersons + " persons and " +
                numEvents + " events to the database.", true);
    }

    /**
     * Factory method that creates a new <code>FillResult</code> object, with <code>success</code>
     * set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed attempt to fill the database
     * @return The new failure <code>FillResult</code> object
     */
    public static FillResult newFailure(String errorMessage) {
        return new FillResult(errorMessage);
    }

    private FillResult(String errorMessage) {
        super(errorMessage, false);
    }
}
