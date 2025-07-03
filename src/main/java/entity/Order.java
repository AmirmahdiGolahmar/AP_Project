package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    private Coupon coupon;

    private String comment;
    private String photo;
    private double rating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;

    public Order(List<CartItem> cartItems, String deliveryAddress,
                 Customer customer, Restaurant restaurant,
                 LocalDateTime createdAt, LocalDateTime updatedAt, OrderStatus status) {
        this.cartItems = cartItems;
        this.deliveryAddress = deliveryAddress;
        this.customer = customer;
        this.restaurant = restaurant;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rating = 0.0;
        this.coupon = null;
        this.delivery = null;
        this.status = status;
    }

    public Order(List<CartItem> cartItems, String deliveryAddress,
                 Customer customer, Restaurant restaurant,
                 LocalDateTime createdAt, LocalDateTime updatedAt, Coupon coupon, OrderStatus status) {
        this.cartItems = cartItems;
        this.deliveryAddress = deliveryAddress;
        this.customer = customer;
        this.restaurant = restaurant;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rating = 0.0;
        this.coupon = coupon;
        this.delivery = null;
        this.status = status;
    }

    public Order() {}

    public Long getRawPrice() {
        return (long) cartItems.stream()
                .mapToDouble(CartItem::getCartItemPrice)
                .sum();
    }
    public Long getPayPrice() {
       long price = (long) (getRawPrice() * (restaurant.getTaxFee() + restaurant.getAdditionalFee()));
       if(coupon == null){
           return price;
       }else{
           double value = (double) coupon.getValue();
           if (coupon.getType().equals(CouponType.fixed)){
               return (long)(price - value);
           }else{
               value = value <= 1 ? value : value / 100;
               return (long)(price * (1 - value));
           }
       }
    }
}
