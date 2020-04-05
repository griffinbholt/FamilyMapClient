package server.exception;

/**
 * An exception thrown in the case of an HTTP Bad Request
 * @author griffinbholt
 */
public class HttpBadRequestException extends Exception {
    /**
     * Constructor with a specified error message
     * @param message The error message
     */
    public HttpBadRequestException(String message)
    {
        super(message);
    }
}
