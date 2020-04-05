package server.exception;

/**
 * An exception thrown in the case of an HTTP Bad Method
 * @author griffinbholt
 */
public class HttpBadMethodException extends HttpBadRequestException {
    private static final String DEFAULT_MESSAGE = "Method not allowed.";

    /**
     * Default constructor
     */
    public HttpBadMethodException() {
        super(DEFAULT_MESSAGE);
    }
}
