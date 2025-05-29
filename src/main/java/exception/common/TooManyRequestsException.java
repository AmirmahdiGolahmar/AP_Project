package exception.common;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {}
    public TooManyRequestsException(String message) {
        super(message);
    }
}
