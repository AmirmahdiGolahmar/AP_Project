package service;

import dao.*;
import dto.*;
import entity.*;
import exception.AlreadyExistsException;
import exception.NotFoundException;
import util.SearchUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import validator.RestaurantValidator.*;

import static util.LocalDateTimeAdapter.StringToDateTime;
import static validator.RestaurantValidator.couponValidator;

public class AdminService {

    private final CouponDao couponDao;
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final TransactionDao transactionDao;

    public AdminService() {
        couponDao = new CouponDao();
        userDao = new UserDao();
        orderDao = new OrderDao();
        transactionDao = new TransactionDao();
    }

    public CouponDto addCoupon(CouponRequest request) {
        if(getAllCoupons().stream().anyMatch(i -> i.getCode().equals(request.getCoupon_code())))
            throw new AlreadyExistsException("This coupon already exists");
        couponValidator(request);
        Coupon coupon = new Coupon();
        String type = request.getType().toLowerCase();
        if(type.equals("fixed")) coupon.setType(CouponType.fixed);
        else coupon.setType(CouponType.percent);
        LocalDateTime start_date = StringToDateTime(request.getStart_date());
        LocalDateTime end_date = StringToDateTime(request.getEnd_date());
        coupon.setCode(request.getCoupon_code());
        coupon.setValue(request.getValue());
        coupon.setMinPrice(request.getMin_price());
        coupon.setUserCount(request.getUser_count());
        coupon.setStartDate(start_date);
        coupon.setEndDate(end_date);

        couponDao.save(coupon);

        return new CouponDto(coupon);
    }

    public List<CouponDto> getAllCoupons() {

        return couponDao.findAll().stream().map(CouponDto::new).collect(Collectors.toList());
    }

    public CouponDto getCoupon(Long id) {
        Coupon coupon = couponDao.findById(id);
        if (coupon == null) throw new NotFoundException("This coupon doesn't exists");
        return new CouponDto(coupon);
    }

    public CouponDto updateCoupon(CouponRequest request, Long id) {
        Coupon coupon = couponDao.findById(id);
        if (coupon == null) throw new NotFoundException("This coupon doesn't exists");
        couponValidator(request);
        coupon.setCode(request.getCoupon_code());
        coupon.setValue(request.getValue());
        coupon.setMinPrice(request.getMin_price());
        coupon.setUserCount(request.getUser_count());
        coupon.setStartDate(StringToDateTime(request.getStart_date()));
        coupon.setEndDate(StringToDateTime(request.getEnd_date()));
        couponDao.update(coupon);
        return new CouponDto(coupon);
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = couponDao.findById(id);
        if (coupon == null) throw new NotFoundException("This coupon doesn't exists");
        couponDao.delete(id);
    }

    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream().map(UserDto::new).toList();
    }

    public void changeUserStatus(Long userId, String status) {
        User user = userDao.findById(userId);
        user.setStatus(status);
        userDao.update(user);
    }

    public List<OrderDto> searchOrders(String search, String vendor, String courier,
                             String customer, String status) {

        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String vendorFilter = (vendor == null || vendor.isBlank()) ? "" : vendor.toLowerCase();
        String courierFilter = (courier == null || courier.isBlank()) ? "" : courier.toLowerCase();
        String customerFilter = (customer == null || customer.isBlank()) ? "" : customer.toLowerCase();
        String statusFilter = (status == null || status.isBlank()) ? "" : status.toLowerCase();

        List<Order> allOrders = orderDao.findAll();

        List<String> searchFields = List.of("deliveryAddress", "coupon.code", "status",
            "delivery.fullName", "restaurant.name", "customer.fullName", "createdAt", "updatedAt");

        Map<String, String> filters = new HashMap<>();
        if (!vendorFilter.isBlank()) filters.put("restaurant.name", vendorFilter);
        if (!courierFilter.isBlank()) filters.put("delivery.fullName", courierFilter);
        if (!customerFilter.isBlank()) filters.put("customer.fullName", customerFilter);
        if (!statusFilter.isBlank()) filters.put("status", statusFilter);


        List<Order> result =
            SearchUtil.search(allOrders, Order.class, searchFilter, searchFields, filters);

        return result.stream().map(OrderDto::new).toList();
    }

    public List<PaymentReceiptDto> searchTransaction(String search, String user, String method, String status) {

        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String userFilter = (user == null || user.isBlank()) ? "" : user.toLowerCase();
        String methodFilter = (method == null || method.isBlank()) ? "" : method.toLowerCase();
        String statusFilter = (status == null || status.isBlank()) ? "" : status.toLowerCase();

        List<Transaction> allTransactions = transactionDao.findAll();

        List<String> searchFields = List.of("paymentMethod", "timestamp", "sender.fullName", "paymentStatus");

        Map<String, String> filters = new HashMap<>();
        if (!methodFilter.isBlank()) filters.put("paymentMethod", methodFilter);
        if (!userFilter.isBlank()) filters.put("sender.fullName", userFilter);
        if (!statusFilter.isBlank()) filters.put("paymentStatus", statusFilter);


        List<Transaction> result =
                SearchUtil.search(allTransactions, Transaction.class, searchFilter, searchFields, filters);

        return result.stream().map(PaymentReceiptDto::new).toList();
    }
}