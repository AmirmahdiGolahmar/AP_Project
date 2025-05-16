package dao;

import entity.Admin;
import jakarta.persistence.EntityManager;

public class AdminDao extends GenericDao<Admin> {
    public AdminDao() {
        super(Admin.class);
    }
}