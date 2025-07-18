package entity;

import exception.InvalidInputException;

public enum UserRole {
    ADMIN,
    SELLER,
    CUSTOMER,
    DELIVERY;

    public static UserRole strToStatus(String str) {
        if (str == null) {
            throw new InvalidInputException("UserRole cannot be null");
        }
        try {
            return UserRole.valueOf(str.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("This UserRole is not valid: " + str);
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();}

}