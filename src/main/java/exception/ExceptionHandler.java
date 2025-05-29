package exception;

import exception.common.AlreadyExistsException;
import exception.common.InvalidInputException;
import exception.common.NotFoundException;

public class ExceptionHandler {
    public static void userNotFound(String info) {
        throw new NotFoundException("User with info : " + info + " not found.");
    }

    public static void invalidInput(String message) {
        throw new InvalidInputException(message);
    }

    public static void alreadyExists(String message) {
        throw new AlreadyExistsException(message);
    }
}
