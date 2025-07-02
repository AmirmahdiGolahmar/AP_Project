package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponRegistrationRequest {
    String coupon_code;
    String type;
    Long value;
    Long min_price;
    Long user_count;
    String start_date;
    String end_date;
}
