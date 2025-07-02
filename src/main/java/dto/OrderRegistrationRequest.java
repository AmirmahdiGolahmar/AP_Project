package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRegistrationRequest {
    String delivery_address;
    long vendor_id;
    long coupon_id;
    List<CartItemDto> items;
}
