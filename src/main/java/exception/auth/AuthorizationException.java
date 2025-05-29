package exception.auth;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
    // Throw when the user is logged in but lacks permission to perform an action
}