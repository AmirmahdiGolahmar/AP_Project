package dao;

import entity.Delivery;
import entity.Seller;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import util.HibernateUtil;

public class DeliveryDao extends GenericDao<Delivery> {
    public DeliveryDao() {
        super(Delivery.class);
    }

//    public Delivery findByMobile(String mobile) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            return session.createQuery(
//                            "FROM Delivery WHERE mobile = :mobile", Delivery.class)
//                    .setParameter("mobile", mobile)
//                    .uniqueResult();
//        }
//    }
}