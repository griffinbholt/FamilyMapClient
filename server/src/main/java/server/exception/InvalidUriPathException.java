package server.exception;

/**
 * An error thrown when the received URI Path is invalid.
 * @author griffinbholt
 */
public class InvalidUriPathException extends HttpBadRequestException {
    private static final String DEFAULT_MESSAGE = "The URI path is invalid";

    /**
     * Constructor with a specified error message
     * @param uriPath The invalid uri path
     */
    public InvalidUriPathException(String uriPath)
    {
        super(DEFAULT_MESSAGE + ": " + uriPath);
    }
}
