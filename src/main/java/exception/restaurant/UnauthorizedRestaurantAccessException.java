package exception.restaurant;

public class UnauthorizedRestaurantAccessException extends RuntimeException {
    public UnauthorizedRestaurantAccessException(String message) {
        super(message);
    }
    // Throw when a seller tries to access or edit a restaurant they do not own
}