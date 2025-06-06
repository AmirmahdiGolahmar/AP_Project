package dao;

import entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class CustomerDao extends GenericDao<Customer> {
    public CustomerDao() {
        super(Customer.class);
    }

    public Customer findByMobile(String mobile) throws NoResultException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Customer WHERE mobile = :mobile", Customer.class)
                    .setParameter("mobile", mobile)
                    .uniqueResult();
        }
    }

}