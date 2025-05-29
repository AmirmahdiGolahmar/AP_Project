package exception.auth;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
    // Throw when the user is authenticated but lacks necessary role/permission
}
