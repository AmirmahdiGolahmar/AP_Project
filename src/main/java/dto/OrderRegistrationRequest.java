package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderRegistrationRequest {
    String delivery_address;
    Long vendor_id;
    Long coupon_id;
    List<CartItemDto> items = new ArrayList<>();
}
