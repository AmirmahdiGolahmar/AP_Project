package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

enum TransactionType {
    PAYMENT,
    WITHDRAWAL,
    WALLET_TOPUP
}

enum PaymentMethod {
    ONLINE,
    WALLET
}


@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDateTime timestamp;

    @Embedded
    private Bank_info bank_info;


    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }
}

