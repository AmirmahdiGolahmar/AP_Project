package dto;

import entity.Restaurant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantDto {
    private Long id;
    private Long seller_id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Double tax_fee;
    private Double additional_fee;


    public RestaurantDto(Restaurant restaurant) {
        if(restaurant.getId() != null) this.id = restaurant.getId();
        if(restaurant.getSeller() != null) this.seller_id = restaurant.getSeller().getId();
        if(restaurant.getName() != null) this.name = restaurant.getName();
        if(restaurant.getAddress() != null) this.address = restaurant.getAddress();
        if(restaurant.getPhone() != null) this.phone = restaurant.getPhone();
        if (restaurant.getLogo() != null) this.logoBase64 = restaurant.getLogo();
        if(restaurant.getTaxFee() != null) this.tax_fee = restaurant.getTaxFee();
        if(restaurant.getAdditionalFee() != null) this.additional_fee = restaurant.getAdditionalFee();
    }
}
