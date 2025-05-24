package validator;


import dto.LoginRequest;
import exception.InvalidInputException;
import dto.UserRegistrationRequest;

import java.util.Map;

public class UserValidator {

    public static void validateUser(UserRegistrationRequest request) {
        if (request.getPassword() == null || request.getPassword().length() < 4) {
            throw new InvalidInputException("Invalid field password");
        }

        if (request.getFullName() == null || request.getFullName().isEmpty()) {
            throw new InvalidInputException("Invalid field full name");
        }

        if (request.getMobile() == null || request.getMobile().length() != 11
                || !request.getMobile().startsWith("09")) {
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

    public static void validateLogin(LoginRequest request) {
        if (request.getMobile() == null || request.getPassword() == null) {
            throw new InvalidInputException("Phone and password must be provided");
        }
    }

}