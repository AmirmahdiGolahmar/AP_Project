package service;

import dao.OrderDao;
import dao.TransactionDao;
import dao.UserDao;
import dto.PaymentReceiptDto;
import dto.PaymentRequestDto;
import entity.*;
import exception.ForbiddenException;
import exception.InvalidInputException;
import dto.TransactionDto;
import exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {

    private final UserDao userDao;
    private final TransactionDao transactionDao;
    private final OrderDao orderDao;

    public TransactionService() {
        userDao = new UserDao();
        transactionDao = new TransactionDao();
        orderDao = new OrderDao();
    }

    public void topUp(Long userId, Long amount) {
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


    public PaymentReceiptDto pay(PaymentRequestDto request, Long userId) {
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getOrder_id() == null) throw new InvalidInputException("Invalid input");
        if(request.getMethod() == null) throw new InvalidInputException("Invalid input");

        User user = userDao.findById(userId);

        Order order = orderDao.findById(request.getOrder_id());
        if(order == null) throw new NotFoundException("This order does not exist");
        if(order.isPaid()) throw new ForbiddenException("This order is already paid");
        if(!order.getCustomer().getId().equals(user.getId())) throw new ForbiddenException("You can't pay for this order");

        PaymentMethod method = PaymentMethod.strToStatus(request.getMethod());

        Transaction transaction = new Transaction();
        transaction.setPaymentMethod(method);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSender(user);
        transaction.setOrder(order);

        try{
            if(method.equals(PaymentMethod.wallet)){
                user.withdraw(order.getPayPrice());
            }
            if(method.equals(PaymentMethod.online)){
                onlinePay();
            }
            transaction.setPaymentStatus(PaymentStatus.success);
            order.setPaid(true);
        }catch(ForbiddenException e){
            transaction.setPaymentStatus(PaymentStatus.failed);
        }
        orderDao.update(order);
        transactionDao.save(transaction);
        userDao.update(user);


        return new PaymentReceiptDto(transaction);
    }

    public boolean onlinePay() {
        return true;
    }
}
