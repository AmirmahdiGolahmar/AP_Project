package dao;

import entity.Seller;
import jakarta.persistence.EntityManager;

public class SellerDao extends GenericDao<Seller> {
    public SellerDao() {
        super(Seller.class);
    }
}
