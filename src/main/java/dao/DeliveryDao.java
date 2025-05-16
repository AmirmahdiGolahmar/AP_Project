package dao;

import entity.Delivery;
import jakarta.persistence.EntityManager;

public class DeliveryDao extends GenericDao<Delivery> {
    public DeliveryDao() {
        super(Delivery.class);
    }
}