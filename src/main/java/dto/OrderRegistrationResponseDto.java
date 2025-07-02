package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRegistrationResponseDto {
    long id;
    String delivery_address;
    long customer_id;
    long vendor_id;
    long coupon_id;
    List<Integer> item_ids;
    long raw_price;
    Double tax_fee;
    Double courier_fee;
    long pay_price;
    long courier_id;
    String status;
    String created_at;
    String updated_at;
}
