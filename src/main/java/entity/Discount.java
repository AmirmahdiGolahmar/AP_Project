package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 12)
    private String code;

    private int maxUsages;

    private int usedCount;

    private double percentage;

    private LocalDateTime expiryDate;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Discount() {
        this.code = generateCode();
        this.usedCount = 0;
    }

    public Discount(String code, int maxUsages, double percentage, LocalDateTime expiryDate, Customer customer) {
        this.code = code;
        this.maxUsages = maxUsages;
        this.usedCount = 0;
        this.percentage = percentage;
        this.expiryDate = expiryDate;
        this.customer = customer;
    }

    public Discount(String code, int maxUsages, double percentage, LocalDateTime expiryDate, Item item) {
        this.code = code;
        this.maxUsages = maxUsages;
        this.usedCount = 0;
        this.percentage = percentage;
        this.expiryDate = expiryDate;
        this.item = item;
    }

    public Discount(String code, int maxUsages, double percentage, LocalDateTime expiryDate, Category category) {
        this.code = code;
        this.maxUsages = maxUsages;
        this.usedCount = 0;
        this.percentage = percentage;
        this.expiryDate = expiryDate;
        this.category = category;
    }

    private String generateCode() {
        return UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    // Getters and Setters

    public Long getId() { return id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getMaxUsages() { return maxUsages; }
    public void setMaxUsages(int maxUsages) { this.maxUsages = maxUsages; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
