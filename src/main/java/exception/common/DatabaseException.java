package exception.common;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
    // Throw when a database operation fails unexpectedly
}
