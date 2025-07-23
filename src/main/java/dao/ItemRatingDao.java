package dao;

import entity.Item;
import entity.ItemRating;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public class ItemRatingDao extends GenericDao<ItemRating> {
    public ItemRatingDao(){
        super(ItemRating.class);
    }

    public List<ItemRating> findAllWithItemAndRestaurant() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT ir FROM ItemRating ir " +
                                    "JOIN FETCH ir.item i " +
                                    "JOIN FETCH i.restaurant", ItemRating.class)
                    .list();
        }
    }

    public List<ItemRating> findByRestaurantId(long restaurantId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT ir FROM ItemRating ir " +
                                    "JOIN FETCH ir.item i " +
                                    "JOIN FETCH i.restaurant r " +
                                    "WHERE r.id = :restaurantId", ItemRating.class)
                    .setParameter("restaurantId", restaurantId)
                    .list();
        }
    }


}