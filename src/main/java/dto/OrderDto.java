package dto;

import entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    List<ItemHelper> items;
    Long raw_price;
    Double tax_fee;
    Double additional_fee;
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
        this.items = order.getCartItems().stream()
                .map(i -> new ItemHelper(i.getItem().getId(), i.getQuantity()))
                .toList();

        this.setRaw_price(order.getRawPrice());

        if(order.getRestaurant().getTaxFee() != null)
            this.setTax_fee(order.getRestaurant().getTaxFee());
        else
            this.setTax_fee(0.0);

        if(order.getRestaurant().getAdditionalFee() != null)
            this.setAdditional_fee(order.getRestaurant().getAdditionalFee());
        else
            this.setAdditional_fee(0.0);

        if(order.getRestaurant().getAdditionalFee() != null)
            this.setCourier_fee(order.getRestaurant().getAdditionalFee()/4);
        else
            this.setCourier_fee(0.0);

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

    static class ItemHelper{
        Long item_id;
        Integer quantity;

        ItemHelper(Long item_id, Integer quantity) {
            this.item_id = item_id;
            this.quantity = quantity;
        }
    }
}
