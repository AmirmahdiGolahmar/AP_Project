package service;

import dao.TransactionDao;
import dao.UserDao;
import entity.Transaction;
import entity.User;
import exception.InvalidInputException;
import dto.TransactionDto;

import java.util.List;

public class TransactionService {

    private final UserDao userDao;
    private final TransactionDao transactionDao;

    public TransactionService() {
        userDao = new UserDao();
        transactionDao = new TransactionDao();
    }

    public void topUp(Long userId, Integer amount) {
        User user = userDao.findById(userId);
        if(amount == null || amount <= 0) throw new InvalidInputException("Invalid amount");
        user.deposit(amount);
        userDao.update(user);
    }

    public List<TransactionDto> getTransactions(Long userId) {
        List<Transaction> allTransactions = transactionDao.findAll().stream().filter(
                t -> t.getSender().getId().equals(userId) ||
                        t.getOrder().getRestaurant().getSeller().getId().equals(userId)
        ).toList();
        return allTransactions.stream().map(TransactionDto::new).toList();
    }


}
