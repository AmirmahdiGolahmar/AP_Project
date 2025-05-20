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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // پرداخت، برداشت، افزایش اعتبار

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // آنلاین یا کیف پول

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    public Transaction() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
}

