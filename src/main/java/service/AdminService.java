package service;

import dao.*;
import dto.CouponRegistrationRequest;
import entity.Coupon;
import entity.CouponType;
import util.LocalDateTimeAdapter.*;

import java.time.LocalDateTime;
import java.util.List;

import static util.LocalDateTimeAdapter.StringToDateTime;

public class AdminService {

    private final CouponDao couponDao;

    public AdminService() {
        couponDao = new CouponDao();
    }

    public Coupon addCoupon(CouponRegistrationRequest request) {
        Coupon coupon = new Coupon();
        String type = request.getType().toLowerCase();
        if(type.equals("fixed")) coupon.setType(CouponType.FIXED);
        else coupon.setType(CouponType.PERCENT);
        LocalDateTime start_date = StringToDateTime(request.getStart_date());
        LocalDateTime end_date = StringToDateTime(request.getEnd_date());
        coupon.setCode(request.getCoupon_code());
        coupon.setType(coupon.getType());
        coupon.setValue(coupon.getValue());
        coupon.setMinPrice(coupon.getMinPrice());
        coupon.setUsedCount(coupon.getUsedCount());
        coupon.setStartDate(start_date);
        coupon.setEndDate(end_date);

        couponDao.save(coupon);

        return coupon;
    }

    public boolean validateCoupon(Coupon coupon, LocalDateTime now) {
        LocalDateTime start = coupon.getStartDate();
        LocalDateTime end = coupon.getEndDate();

        return (now.isEqual(start) || now.isAfter(start)) &&
                (now.isEqual(end) || now.isBefore(end));
    }


    public List<Coupon> getAllCoupons() {
        return couponDao.findAll();
    }
}