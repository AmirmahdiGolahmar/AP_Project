package exception.token;

public class TokenBlacklistException extends RuntimeException {
    public TokenBlacklistException(String message) {
        super(message);
    }
    // Throw when a token has been blacklisted but is used again
}