package dao;

import entity.Customer;
import entity.User;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class CustomerDao extends GenericDao<Customer> {
    public CustomerDao() {
        super(Customer.class);
    }

//    public Customer findByMobile(String mobile) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            return session.createQuery(
//                            "FROM Customer WHERE mobile = :mobile", Customer.class)
//                    .setParameter("mobile", mobile)
//                    .uniqueResult();
//        }
//    }

    public User findByIdLoadCartItems(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT u FROM User u LEFT JOIN FETCH u.cartItems WHERE u.id = :id", User.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

}