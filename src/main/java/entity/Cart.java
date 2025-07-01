package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    private String userNote;

    private String deliveryAddress;

    private double totalPrice;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

//    @ManyToOne
//    @JoinColumn(name = "restaurant_id", nullable = false)
//    private Restaurant restaurant;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<CartItem> cartItems;

    public Cart() {
        this.createdAt = LocalDateTime.now();
    }

    public Cart(Long id, String userNote, String deliveryAddress, double totalPrice, LocalDateTime createdAt,
                Customer customer) {
        this.id = id;
        this.userNote = userNote;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt;
        this.customer = customer;
        this.totalPrice = 0;
    }

    public double getTotalPrice() {
        for (CartItem cartItem : cartItems){
            this.totalPrice += cartItem.getTotalPriceCartItem();
        }
        return totalPrice;
    }

    public void addCartItems(List<CartItem> newItems) {
        if (newItems == null || newItems.isEmpty()) return;

        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>();
        }

        this.cartItems.addAll(newItems);
    }
}
