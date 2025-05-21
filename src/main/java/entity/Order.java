package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "confirmed_orders")
public class Order {

    @Id
    private Long id; // همون شناسه‌ی Cart (سبد خرید)

    @OneToOne
    @MapsId // برای اینکه id این کلاس، id همون cart باشه
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String customerName;

    private String customerPhone;

    private String assignedTo; // مثلاً نام پیک

    private LocalDateTime confirmedAt;

    // Constructors
    public Order() {
        this.confirmedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Getters & Setters

    public Long getId() { return id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
}
