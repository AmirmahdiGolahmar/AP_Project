package exception.common;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
    // Throw as a generic fallback for unexpected server errors
}