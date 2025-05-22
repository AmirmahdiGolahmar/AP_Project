package validator;


import exception.InvalidInputException;
import dto.UserRegistrationRequest;

public class UserValidator {

    public static void validateUser(UserRegistrationRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            throw new InvalidInputException("Invalid field password");
        }

        if (request.getFullName() == null || request.getFullName().isEmpty()) {
            throw new InvalidInputException("Invalid field full name");
        }

        if (request.getMobile() == null || request.getMobile().length() != 11) {
            throw new InvalidInputException("Invalid field mobile");
        }

        if (request.getEmail() == null || !request.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidInputException("Invalid field email");
        }

        if (request.getRole() == null ||
                !(request.getRole().equalsIgnoreCase("customer") ||
                        request.getRole().equalsIgnoreCase("buyer") ||
                        request.getRole().equalsIgnoreCase("seller") ||
                        request.getRole().equalsIgnoreCase("delivery"))) {
            throw new InvalidInputException("Invalid field role");
        }
    }
}