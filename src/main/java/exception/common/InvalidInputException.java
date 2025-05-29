package exception.common;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
    // Throw when request data is invalid or contains missing/incorrect fields
}