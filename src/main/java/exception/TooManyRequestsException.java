package exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {}
    public TooManyRequestsException(String message) {
        super(message);
    }
}
