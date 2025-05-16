package dao;

import entity.Account;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class AccountDao extends GenericDao<Account> {
    public AccountDao() {
        super(Account.class);
    }
}
