package shared.result;

/**
 * A superclass that represents the message and success data of the result of a data request.
 * Never used outside of its subclasses.
 * @author griffinbholt
 */
public abstract class Result {
    /**
     * The message of the result
     */
    private String message;

    /**
     * True, if the request succeeded; false, otherwise
     */
    private final boolean success;

    Result(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters
    public String getMessage() {
        return this.message;
    }

    public boolean isSuccess() {
        return this.success;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }
}
