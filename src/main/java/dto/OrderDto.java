package dto;

import entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static util.LocalDateTimeAdapter.TimeToString;

@Getter
@Setter
public class OrderDto {
    Long id;
    String delivery_address;
    Long customer_id;
    Long vendor_id;
    Long coupon_id;
    List<Long> item_ids;
    Long raw_price;
    Double tax_fee;
    Double addtional_fee;
    Double courier_fee;
    Long pay_price;
    Long courier_id;
    String status;
    String created_at;
    String updated_at;

    public OrderDto(Order order) {
        this.setId(order.getId());
        this.setDelivery_address(order.getDeliveryAddress());
        this.setCustomer_id(order.getCustomer().getId());
        this.setVendor_id(order.getRestaurant().getId());
        if(order.getCoupon() != null) {
            this.setCoupon_id(order.getCoupon().getId());
        }else{
            this.setCoupon_id(null);
        }
        this.setItem_ids(order.getCartItems().stream().map(i -> i.getItem().getId()).collect(Collectors.toList()));
        this.setRaw_price(order.getRawPrice());
        this.setTax_fee(order.getRestaurant().getTaxFee());
        this.setAddtional_fee(order.getRestaurant().getAdditionalFee());
        this.setCourier_fee(order.getRestaurant().getAdditionalFee());
        this.setPay_price(order.getPayPrice());
        if(order.getDelivery() != null) {
            this.setCourier_id(order.getDelivery().getId());
        }else{
            this.setCourier_id(null);
        }
        this.setStatus(order.getStatus().toString());
        this.setCreated_at(TimeToString(order.getCreatedAt()));
        this.setUpdated_at(TimeToString(order.getUpdatedAt()));
    }
}
