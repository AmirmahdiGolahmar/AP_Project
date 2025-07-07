package dao;

import dao.GenericDao;
import entity.Customer;
import entity.User;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class UserDao extends GenericDao<User> {
    public UserDao() {
        super(User.class);
    }

    public User findByIdLoadFavorites(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT u FROM User u LEFT JOIN FETCH u.favoriteRestaurants WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

}