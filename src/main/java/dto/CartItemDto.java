package dto;

import entity.CartItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    Long item_id;
    int quantity;

    public CartItemDto(CartItem cartItem) {
        this.item_id = cartItem.getItem().getId();
        this.quantity = cartItem.getQuantity();
    }

    public CartItemDto(Long item_id,  int quantity) {
        this.item_id = item_id;
        this.quantity = quantity;
    }
}
