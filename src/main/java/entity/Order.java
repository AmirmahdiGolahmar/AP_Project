package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
public class Order {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @OneToOne
    @MapsId
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Setter
    @Getter
    private String address;
    @Setter
    @Getter
    private double rawPrice;
    @Setter
    @Getter
    private double taxFee;
    @Setter
    @Getter
    private double deliveryFee;
    @Setter
    @Getter
    private double totalPrice;

    @Setter
    @Getter
    @ManyToOne
    private Customer customer;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery assignedTo;

    @Setter
    @Getter
    private LocalDateTime confirmedAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order")
    private List<Comment> comments;

    public Order() {}

    public Order(Cart cart, String address, double rawPrice, double taxFee,
                 double deliveryFee, double totalPrice, Customer customer, Delivery assignedTo) {
        this.cart = cart;
        this.status = OrderStatus.PENDING;
        this.confirmedAt = LocalDateTime.now();
        this.address = address;
        this.rawPrice = rawPrice;
        this.taxFee = taxFee;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.customer = customer;
        this.assignedTo = assignedTo;
        this.comments = new ArrayList<>();
    }

}
