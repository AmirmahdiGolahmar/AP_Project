package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.sql.exec.spi.StandardEntityInstanceResolver;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "orders")
@Setter
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Setter(AccessLevel.PRIVATE)
    private List<CartItem> cartItems;

    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Delivery delivery;

    @ManyToOne
    private Restaurant restaurant;

    private String comment;
    private String photo;
    private double rating;

    @Setter(AccessLevel.PRIVATE)
    private Long totalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;

    public Order(List<CartItem> cartItems, String deliveryAddress,
                 Customer customer, Restaurant restaurant,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.cartItems = cartItems;
        this.deliveryAddress = deliveryAddress;
        this.customer = customer;
        this.restaurant = restaurant;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rating = 0.0;
        this.setTotalPrice();
    }

    public Order() {}

    public void setTotalPrice() {
        this.totalPrice = (long) cartItems.stream()
                .mapToDouble(CartItem::getCartItemPrice)
                .sum();
    }
}
