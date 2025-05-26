package dao;
import entity.Item;

public class ItemDao extends GenericDao<Item> {
    public ItemDao() {
        super(Item.class);
    }
}
