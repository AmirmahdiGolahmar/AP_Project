package entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 12)
    private String code;

    private int quantity;

    private int usedCount;

    private double amount;
    private double percentage;

    private LocalDateTime createdAt;
    private int duration;


    @ManyToOne
    @JoinColumn(name = "customer_mobile")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Discount() {
        this.code = generateCode();
        this.usedCount = 0;
    }

    public Discount(int quantity, double amount, double percentage, LocalDateTime createdAt,
                    int duration, Customer customer, Item item, Category category, Restaurant restaurant) {
        this.code = generateCode();
        this.quantity = quantity;
        this.usedCount = 0;
        this.amount = amount; //nullable
        this.percentage = percentage; //nullable
        this.createdAt = LocalDateTime.now();
        this.duration = duration; //nullable
        this.customer = customer; //nullable
        this.item = item; //nullable
        this.category = category; //nullable
        this.restaurant = restaurant; //nullable
    }

    private String generateCode() {
        return UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
