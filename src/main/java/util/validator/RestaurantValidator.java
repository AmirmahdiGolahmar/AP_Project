package util.validator;

import dao.CouponDao;
import dao.ItemDao;
import dao.OrderDao;
import dao.RestaurantDao;
import dto.*;
import entity.*;
import exception.InvalidInputException;
import exception.NotFoundException;

import java.io.IOException;
import java.time.LocalDate;

import static util.LocalDateTimeAdapter.matchesDateFormat;
import static util.validator.validator.additionalFeeValidator;
import static util.validator.validator.taxFeeValidator;

public class RestaurantValidator {
    public static void validateItemRegistrationRequest(ItemDto request) throws IOException {
        if(request == null) throw new InvalidInputException("request is null");
        if(request.getName() == null || request.getName().isBlank()) throw new InvalidInputException("Invalid name");
        if(request.getPrice() == null || request.getPrice() < 0) throw new InvalidInputException("Invalid price");
        if(request.getDescription() == null || request.getDescription().isBlank()) throw new InvalidInputException("Invalid description");
        if(request.getSupply() == null || request.getSupply() < 0) throw new InvalidInputException("Invalid supply");
        if(request.getKeywords() == null || request.getKeywords().isEmpty()) throw new InvalidInputException("Invalid keywords");
    }

    public static void validateMenuRegistrationRequest(MenuRegistrationDto request) throws IOException {
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getTitle() == null ||  request.getTitle().isBlank())
            throw new InvalidInputException("Invalid title");
    }

    public static void validateItemAddToMenuRequest(ItemAddToMenuRequestDto request){
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getItem_id() == null) throw new InvalidInputException("Invalid item id");
    }

    public static Coupon validateCouponId(Long CouponId) {
        if(CouponId != null && CouponId < 0) throw new InvalidInputException("Invalid coupon");
        Coupon coupon = new CouponDao().findById(CouponId);
        if(coupon == null) throw new InvalidInputException("Invalid coupon");
        return coupon;
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

    public static void validateRestaurantRegistrationRequest(RestaurantRegistrationRequest request) {
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getName() == null || request.getName().isBlank()) throw new InvalidInputException("Invalid name");
        if(request.getAddress() == null || request.getAddress().isBlank()) throw new InvalidInputException("Invalid address");
        if(request.getPhone() == null || request.getPhone().isBlank()) throw new InvalidInputException("Invalid Phone");

        if(request.getTax_fee() != null)
            taxFeeValidator(request.getTax_fee());

        if(request.getAdditional_fee() != null)
            additionalFeeValidator(request.getAdditional_fee());

    }

    public static Restaurant validateRestaurant(Long restaurantId) {
        Restaurant restaurant = new RestaurantDao().findById(restaurantId);
        if (restaurant == null) {
            throw new NotFoundException("Restaurant doesn't exist");
        }

        return restaurant;
    }

    public static Item validateItem(Long itemId, Restaurant restaurant) {
        if(restaurant == null){
            Item item = new ItemDao().findById(itemId);
            if (item == null) throw new NotFoundException("Item doesn't exist");
            return item;
        }

       return restaurant.getItems().stream().
               filter(item -> item.getId().equals(itemId)).findFirst()
               .orElseThrow(() -> new NotFoundException("Item doesn't exist"));
    }

    public static void validateOrderRegistrationRequest(OrderRegistrationRequest request) {
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getVendor_id() == null || request.getVendor_id() < 0) throw new InvalidInputException("Invalid vendor");
        if(request.getDelivery_address() == null || request.getDelivery_address().isBlank()) throw new InvalidInputException("Invalid address");
        if(request.getItems().isEmpty()) throw new InvalidInputException("Invalid items");
        if(request.getCoupon_id() != null && request.getCoupon_id() < 0) throw new InvalidInputException("Invalid coupon");
    }

    public static Order validateOrder(Long orderId) {
        if(orderId == null) throw new InvalidInputException("Invalid order id");
        Order order = new OrderDao().findById(orderId);
        if (order == null) throw new NotFoundException("Order doesn't exist");
        return order;
    }

    public static void validateRatingRegistrationRequest(OrderRatingDto request) {
        if(request == null) throw new InvalidInputException("Invalid request");
        if(request.getOrder_id() == null || request.getOrder_id() < 0) throw new InvalidInputException("Invalid order id");
        if(request.getRating() < 0 || request.getRating() > 5) throw new InvalidInputException("Invalid rating");
        if(request.getComment() == null || request.getComment().isBlank()) throw new InvalidInputException("Invalid comment");
    }
}
