package entity;

import jakarta.persistence.*;
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
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customerMobile")
    private Customer customer;

    private TransactionType transactionType;

    public Transaction() {}
    public Transaction(Customer customer, TransactionType transactionType) {
        this.customer = customer;
        this.transactionType = transactionType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}

