package dao;
import entity.Item;
import entity.Restaurant;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public class ItemDao extends GenericDao<Item> {
    public ItemDao() {
        super(Item.class);
    }

    public List<Item> findAllWithRestaurant() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT i FROM Item i " +
                                    "JOIN FETCH i.restaurant", Item.class)
                    .list();
        }
    }


}
