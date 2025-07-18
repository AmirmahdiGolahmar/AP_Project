package util.validator;


import exception.InvalidInputException;
import dto.UserRegistrationRequest;

import static util.validator.validator.*;

public class UserValidator {

    public static void validateUserRegistrationRequest(UserRegistrationRequest request) {
        passwordValidator(request.getPassword());
        fullNameValidator(request.getFull_name());
        mobileValidator(request.getMobile());
        if(request.getEmail() != null) emailValidator(request.getEmail());
        roleValidator(request.getRole());
        addressValidator(request.getAddress());
    }

}