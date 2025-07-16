package entity;

import exception.InvalidInputException;

public enum PaymentMethod {
    wallet,
    online;

    public static PaymentMethod strToStatus(String str) {
        if (str == null) {
            throw new InvalidInputException("Status cannot be null");
        }
        try {
            return PaymentMethod.valueOf(str.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("This status is not valid: " + str);
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
