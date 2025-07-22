package util.validator;


import entity.BankInfo;
import exception.InvalidInputException;
import dto.UserRegistrationRequest;

import static util.validator.validator.*;

public class UserValidator {

    public static void validateUserRegistrationRequest(UserRegistrationRequest request) {
        if(request == null) throw new InvalidInputException("Invalid request");
        passwordValidator(request.getPassword());
        fullNameValidator(request.getFull_name());
        mobileValidator(request.getMobile());
        if(request.getEmail() != null && !request.getEmail().isBlank()) emailValidator(request.getEmail());
        roleValidator(request.getRole());
        addressValidator(request.getAddress());
    }

}