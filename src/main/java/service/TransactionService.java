package service;

import dao.OrderDao;
import dao.TransactionDao;
import dao.UserDao;
import dto.AmountDto;
import dto.PaymentReceiptDto;
import dto.PaymentRequestDto;
import entity.*;
import exception.ForbiddenException;
import exception.InvalidInputException;
import dto.TransactionDto;
import exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static util.validator.validator.bankInfoValidator;

public class TransactionService {

    private final UserDao userDao;
    private final TransactionDao transactionDao;
    private final OrderDao orderDao;

    public TransactionService() {
        userDao = new UserDao();
        transactionDao = new TransactionDao();
        orderDao = new OrderDao();
    }

    public void topUp(User user, AmountDto request) {
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getAmount() == null || request.getAmount() <= 0)
            throw new InvalidInputException("Invalid amount");
        if(user.getBankInfo() == null) throw new ForbiddenException("Complete your bank info first");
        else bankInfoValidator(user.getBankInfo());
        user.deposit(request.getAmount());
        userDao.update(user);
    }

    public List<TransactionDto> getTransactions(User user) {
        if(user.getBankInfo() == null) throw new ForbiddenException("Complete your bank info first");
        else bankInfoValidator(user.getBankInfo());

        List<Transaction> allTransactions = transactionDao.findAll().stream().filter(
                t -> t.getSender().getId().equals(user.getId()) ||
                        t.getOrder().getRestaurant().getSeller().getId().equals(user.getId())
        ).toList();
        return allTransactions.stream().map(TransactionDto::new).toList();
    }

    public PaymentReceiptDto pay(PaymentRequestDto request, User user) {

        if(user.getBankInfo() == null) throw new InvalidInputException("Complete your bank info first");
        else bankInfoValidator(user.getBankInfo());

        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getOrder_id() == null) throw new InvalidInputException("Invalid input");
        if(request.getMethod() == null) throw new InvalidInputException("Invalid input");

        Order order = orderDao.findById(request.getOrder_id());
        if(order == null) throw new NotFoundException("This order does not exist");
        if(order.isPaid()) throw new ForbiddenException("This order is already paid");
        if(!order.getCustomer().getId().equals(user.getId())) throw new ForbiddenException("You can't pay for this order");

        Seller seller = order.getRestaurant().getSeller();

        PaymentMethod method = PaymentMethod.strToStatus(request.getMethod());

        Transaction transaction = new Transaction();
        transaction.setPaymentMethod(method);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setSender(user);
        transaction.setOrder(order);

        try{
            if(method.equals(PaymentMethod.wallet)){
                user.withdraw(order.getPayPrice());
                seller.deposit(order.getPayPrice());
            }
            if(method.equals(PaymentMethod.online)){
                onlinePay();
                seller.deposit(order.getPayPrice());
            }
            transaction.setPaymentStatus(PaymentStatus.success);
            order.setPaid(true);
        }catch(ForbiddenException e){
            transaction.setPaymentStatus(PaymentStatus.failed);
        }
        orderDao.update(order);
        transactionDao.save(transaction);
        userDao.update(user);
        userDao.update(seller);


        return new PaymentReceiptDto(transaction);
    }

    public boolean onlinePay() {
        return true;
    }

    public Double getBalance(User user) {
        if(user.getBankInfo() == null) throw new InvalidInputException("Complete your bank info first");
        else bankInfoValidator(user.getBankInfo());
        return user.getBankInfo().getBalance();
    }
}
