package dto;


import entity.Restaurant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Double tax_fee;
    private Double additional_fee;

    public RestaurantResponse() {}

    public RestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
        this.logoBase64 = restaurant.getLogo();
        this.tax_fee = restaurant.getTaxFee();
        this.additional_fee = restaurant.getAdditionalFee();
    }
}
