package shared.http;

/**
 * A singleton class that contains all of the base URL paths needed for connections between the client and server.
 *
 * @author griffinbholt
 */
public class FamilyMapUrl {
    private FamilyMapUrl() {}

    /**
     * The base URL path for requests to clear all of the data in the server's database
     */
    public final static String CLEAR = "/clear";

    /**
     * The base URL path for requests for event data from the server
     */
    public final static String EVENT = "/event";

    /**
     * The base URL path for requests to fill the database with genealogical data for a specific user
     */
    public final static String FILL = "/fill";

    /**
     * The base URL for requests to load data into the server's database
     */
    public final static String LOAD = "/load";

    /**
     * The base URL for requests to login a user
     */
    public final static String LOGIN = "/user/login";

    /**
     * The base URL for requests for person data from the server
     */
    public final static String PERSON = "/person";

    /**
     * The base URL for requests to a register a user in the server
     */
    public final static String REGISTER = "/user/register";

    /**
     * The default base URL
     */
    public final static String DEFAULT_PATH = "/";
}
