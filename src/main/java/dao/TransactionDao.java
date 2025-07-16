package dao;

import entity.Transaction;

public class TransactionDao extends GenericDao<Transaction> {
    public TransactionDao() {
        super(Transaction.class);
    }
}
