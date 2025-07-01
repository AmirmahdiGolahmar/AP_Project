package dto;

import entity.Restaurant;

public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Double tax_fee;
    private Double additional_fee;


    public RestaurantDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.phone = restaurant.getPhone();
        this.logoBase64 = restaurant.getLogo();
        this.tax_fee = restaurant.getTaxFee();
        this.additional_fee = restaurant.getAdditionalFee();
    }

    public RestaurantDto(long id, String name, String address, String phone, String logoBase64, Double tax_fee) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.logoBase64 = logoBase64;
        this.tax_fee = tax_fee;
        this.additional_fee = 0.0;
    }
}
