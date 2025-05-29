package exception.auth;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message) {
        super(message);
    }
    //When the token is missing, expired, or invalid.
}
