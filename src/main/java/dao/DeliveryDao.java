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
}