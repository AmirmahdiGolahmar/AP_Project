package util.validator;

import dao.CouponDao;
import dto.CouponRequest;
import dto.ItemDto;
import exception.InvalidInputException;

import java.time.LocalDate;

import static util.LocalDateTimeAdapter.matchesDateFormat;

public class RestaurantValidator {
    public static void itemValidator(ItemDto item) {
        if(item.getName() == null || item.getName().equals("")) throw new InvalidInputException("Invalid name");
        if(item.getPrice() < 0) throw new InvalidInputException("Invalid price");
        if(item.getDescription() == null || item.getDescription().equals("")) throw new InvalidInputException("Invalid description");
        if(item.getSupply() < 0) throw new InvalidInputException("Invalid supply");
        if(item.getImageBase64() == null || item.getImageBase64().equals("")) throw new InvalidInputException("Invalid image");
        if(item.getKeywords().isEmpty()) throw new InvalidInputException("Invalid keywords");
    }

    public static void validateCouponId(Long CouponId) {
        if(CouponId == null)  return;
        if(CouponId < 0) throw new InvalidInputException("Invalid coupon");
        CouponDao couponDao = new CouponDao();
        if(couponDao.findById(CouponId) == null) throw new InvalidInputException("Invalid coupon");
    }

    public static void couponValidator(CouponRequest req) {
        String format = "yyyy-MM-dd";
        if (req.getCoupon_code() == null || req.getCoupon_code().equals("")) throw new InvalidInputException("Invalid coupon code");
        if(req.getType() == null || req.getType().equals("")) throw new InvalidInputException("Invalid type");
        if (!req.getType().equalsIgnoreCase("fixed") && !req.getType().equalsIgnoreCase("percent")) throw new InvalidInputException("Invalid type");
        if(req.getUser_count() == null || req.getUser_count() < 0) throw new InvalidInputException("Invalid user count");
        if(req.getValue() == null || req.getValue() < 0) throw new InvalidInputException("Invalid value");
        if(!matchesDateFormat(req.getStart_date(), format) ||
        !matchesDateFormat(req.getEnd_date(), format)) throw new InvalidInputException("Invalid start date");

        LocalDate startDate = LocalDate.parse(req.getStart_date());
        LocalDate endDate = LocalDate.parse(req.getEnd_date());

        if(startDate.isAfter(endDate)) throw new InvalidInputException("Invalid start date");
    }
}
