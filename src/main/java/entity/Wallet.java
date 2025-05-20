package entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    // Constructors
    public Wallet() {}

    // Getters and Setters

    public Long getId() { return id; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
}
