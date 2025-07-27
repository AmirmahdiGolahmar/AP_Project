package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponApplyDto {
    Long order_id;
    String coupon_code;
}
