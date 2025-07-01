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

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id")
    private Cart cart;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Delivery delivery;

    @ManyToOne
    private Seller seller;

    private LocalDateTime confirmedAt;

    public Order() {
        this.confirmedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

}
