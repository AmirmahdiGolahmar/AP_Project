package dao;

import entity.Restaurant;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class RestaurantDao extends GenericDao<Restaurant> {
    public RestaurantDao() {
        super(Restaurant.class);
    }

    public static List<Restaurant> findAllRestaurantsBySellerId(Long sellerId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            return session.createQuery(
                            "FROM Restaurant r WHERE r.seller.id = :sellerId", Restaurant.class)
                    .setParameter("sellerId", sellerId)
                    .list();
        }finally {
            session.close();
        }
    }

}
