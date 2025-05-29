package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class BankInfo {

    private String accountNumber;
    private String bankName;
    private Double balance;

    public BankInfo(String bankName, String accountNumber) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.balance = 0.0;
    }
}
