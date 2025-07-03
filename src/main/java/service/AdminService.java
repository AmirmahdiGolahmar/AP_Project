package service;

import dao.*;
import dto.CouponDto;
import dto.CouponRequest;
import entity.Coupon;
import entity.CouponType;
import exception.AlreadyExistsException;
import exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import validator.RestaurantValidator.*;

import static util.LocalDateTimeAdapter.StringToDateTime;
import static validator.RestaurantValidator.couponValidator;

public class AdminService {

    private final CouponDao couponDao;

    public AdminService() {
        couponDao = new CouponDao();
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
}