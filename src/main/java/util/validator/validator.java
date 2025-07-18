package util.validator;

import exception.InvalidInputException;
import jakarta.persistence.criteria.CriteriaBuilder;

public class validator {
    public static void mobileValidator(String mobile) {
        if (mobile == null || !mobile.matches("^09\\d{9}$")) {
            throw new InvalidInputException("Invalid field mobile");
        }
    }

    public static void emailValidator(String email) {
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidInputException("Invalid field email");
        }
    }

    public static void passwordValidator(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidInputException("Invalid field password");
        }
    }

    public static void fullNameValidator(String fulName) {
        if (fulName == null || fulName.isBlank()) {
            throw new InvalidInputException("Invalid field full name");
        }
    }

    public static void roleValidator(String role) {
        if (role == null ||
                !(role.equalsIgnoreCase("customer") ||
                        role.equalsIgnoreCase("courier") ||
                        role.equalsIgnoreCase("buyer") ||
                        role.equalsIgnoreCase("seller") ||
                        role.equalsIgnoreCase("delivery"))
        )   throw new InvalidInputException("Invalid field role");
    }

    public static void addressValidator(String address) {
        if(address == null || address.isBlank()) {
            throw new InvalidInputException("Invalid field address");
        }
    }

    public static void priceValidator(Double price) {
        if(price == null || price < 0) throw new InvalidInputException("Invalid field price");
    }

    public static void supplyValidator(Integer supply) {
        if(supply == null || supply < 0) throw new InvalidInputException("Invalid field supply");
    }

    public static void taxFeeValidator(Double  taxFee) {
        if(taxFee != null && (taxFee < 0 || taxFee > 100)) {
            throw new InvalidInputException("Invalid field tax fee");
        }
    }

    public static void additionalFeeValidator(Double  additionalFee) {
        if(additionalFee != null && (additionalFee < 0 || additionalFee > 100)) {
            throw new InvalidInputException("Invalid field additionalFee");
        }
    }
}
