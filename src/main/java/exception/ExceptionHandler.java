package exception;

public class ExceptionHandler {
    public static void userNotFound(String info) {
        throw new UserNotFoundException("User with info : " + info + " not found.");
    }

    public static void invalidInput(String message) {
        throw new InvalidInputException(message);
    }

    public static void alreadyExists(String message) {
        throw new AlreadyExistsException(message);
    }
}
