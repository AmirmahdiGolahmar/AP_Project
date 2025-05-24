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

    public User findByMobile(String mobile) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM User WHERE mobile = :mobile", User.class)
                    .setParameter("mobile", mobile)
                    .uniqueResult();
        }
    }

}