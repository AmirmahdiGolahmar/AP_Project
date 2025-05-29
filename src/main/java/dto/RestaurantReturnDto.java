package dto;

import entity.Restaurant;

public class RestaurantReturnDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Integer tax_fee;
    private Integer additional_fee;


    public RestaurantReturnDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
        this.logoBase64 = restaurant.getLogo();
        this.tax_fee = restaurant.getTaxFee();
        this.additional_fee = restaurant.getAdditionalFee();
    }
}
