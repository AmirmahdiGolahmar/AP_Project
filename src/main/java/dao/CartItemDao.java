package dao;

import dto.CartItemDto;
import entity.CartItem;

public class CartItemDao extends GenericDao<CartItem> {
    public CartItemDao() {
        super(CartItem.class);
    }
}
