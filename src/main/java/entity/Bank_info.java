package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Bank_info {

    private String account_number;
    private String bank_name;
    private Double balance;

    public Bank_info(String bank_name, String account_number) {
        this.bank_name = bank_name;
        this.account_number = account_number;
        this.balance = 0.0;
    }
}
