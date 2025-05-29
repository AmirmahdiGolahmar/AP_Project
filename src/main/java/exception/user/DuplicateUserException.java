package exception.user;

public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException(String message) {
        super(message);
    }
    // Throw when trying to register a user with an already existing mobile/email
}