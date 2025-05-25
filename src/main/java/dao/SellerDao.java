package dao;

import entity.Customer;
import entity.Seller;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import util.HibernateUtil;

public class SellerDao extends GenericDao<Seller> {
    public SellerDao() {
        super(Seller.class);
    }

//    public Seller findByMobile(String mobile) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            return session.createQuery(
//                            "FROM Seller WHERE mobile = :mobile", Seller.class)
//                    .setParameter("mobile", mobile)
//                    .uniqueResult();
//        }
//    }
}
