package exception.common;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
    // Throw when trying to create a resource that already exists
}