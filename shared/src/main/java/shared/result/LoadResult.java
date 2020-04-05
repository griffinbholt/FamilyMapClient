package shared.result;

import shared.request.LoadRequest;

/**
 * The result of a {@link LoadRequest load request}
 * Inherits from {@link Result Result}.
 * @author griffinbholt
 */
public final class LoadResult extends Result {
    /**
     * Factory method that creates a new <code>LoadResult</code> object, with <code>success</code>
     * set to <code>true</code>
     * @param numUsers The number of users successfully added to the database
     * @param numPersons The number of persons successfully added to the database
     * @param numEvents The number of events successfully added to the database
     * @return The new success <code>LoadResult</code> object
     */
    public static LoadResult newSuccess(int numUsers, int numPersons, int numEvents) {
        return new LoadResult(numUsers, numPersons, numEvents);
    }

    private LoadResult(int numUsers, int numPersons, int numEvents) {
        super("Successfully added " + numUsers + " users, " + numPersons + " persons, and " +
                numEvents + " events to the database.", true);
    }

    /**
     * Factory method that creates a new <code>LoadResult</code> object, with <code>success</code>
     * set to <code>false</code>
     * @param errorMessage The error message accounting for the reason behind the failed attempt to fill the database
     * @return The new failure <code>LoadResult</code> object
     */
    public static LoadResult newFailure(String errorMessage) {
        return new LoadResult(errorMessage);
    }

    private LoadResult(String errorMessage) {
        super(errorMessage, false);
    }
}
