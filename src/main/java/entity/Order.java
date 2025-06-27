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

}
