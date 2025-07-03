package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    public Long getCartItemPrice() { return (long) this.item.getPrice()*quantity; }

    public CartItem() {}

    public CartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}
