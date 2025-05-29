package exception.user;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
    // Throw when user login fails due to incorrect password or mobile
}