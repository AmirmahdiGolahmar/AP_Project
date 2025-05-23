package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery assignedTo;

    private LocalDateTime confirmedAt;

    public Order() {
        this.confirmedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Long getId() { return id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Delivery getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Delivery assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
}
