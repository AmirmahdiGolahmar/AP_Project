package entity;

import exception.ForbiddenException;
import exception.InvalidInputException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Embeddable
public class BankInfo {

    private String accountNumber;
    private String bankName;
    @Setter(AccessLevel.NONE)
    private Double balance;


    public BankInfo() {
        this.balance = 0.0;
    }

    public BankInfo(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.balance = 0.0;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if(this.balance - amount <= 0) throw new ForbiddenException("Insufficient balance.");
        this.balance -= amount;
    }
}
