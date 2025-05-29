package dao;

import entity.Restaurant;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.Stack;

public class RestaurantDao extends GenericDao<Restaurant> {
    public RestaurantDao() {
        super(Restaurant.class);
    }

    public static List<Restaurant> findAllRestaurantsBySellerId(Long sellerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Restaurant r WHERE r.seller.id = :sellerId";
            return session.createQuery(hql, Restaurant.class)
                    .setParameter("sellerId", sellerId)
                    .getResultList();
        }
    }


}
