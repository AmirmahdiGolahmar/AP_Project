package dao;

import entity.Customer;
import jakarta.persistence.EntityManager;

public class CustomerDao extends GenericDao<Customer> {
    public CustomerDao() {
        super(Customer.class);
    }
}