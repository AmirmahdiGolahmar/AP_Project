package dto;

import entity.Coupon;
import entity.CouponType;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CouponDto {
    private Long id;
    private String code;
    private String type;
    private Long value;
    private Long min_price;
    private Long user_count;

    private String start_date;
    private String end_date;

    public CouponDto() {}
    public CouponDto(Coupon coupon) {
        this.id = coupon.getId();
        this.code = coupon.getCode();
        this.type = coupon.getType().toString();
        this.value = coupon.getValue();
        this.min_price = coupon.getMinPrice();
        this.user_count = coupon.getUserCount();
        this.start_date = util.LocalDateTimeAdapter.DateTimeToString(coupon.getStartDate());
        this.end_date = util.LocalDateTimeAdapter.DateTimeToString(coupon.getEndDate());
    }
}

