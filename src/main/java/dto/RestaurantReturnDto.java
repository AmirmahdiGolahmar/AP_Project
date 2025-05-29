package dto;

import entity.Restaurant;

public class RestaurantReturnDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Integer taxFee;
    private Integer additionalFee;


    public RestaurantReturnDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
        this.logoBase64 = restaurant.getLogo();
        this.taxFee = restaurant.getTaxFee();
        this.additionalFee = restaurant.getAdditionalFee();
    }
}
