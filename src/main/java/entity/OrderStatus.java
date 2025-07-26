package entity;

import exception.InvalidInputException;

public enum OrderStatus {
    submitted,
    unpaid_and_cancelled,
    waiting_vendor,
    cancelled,
    finding_courier,
    on_the_way,
    completed, accepted, rejected, served, unpaid;

    public static OrderStatus strToStatus(String str) {
        if (str == null) {
            throw new InvalidInputException("Status cannot be null");
        }
        try {
            return OrderStatus.valueOf(str.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("This status is not valid: " + str);
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
