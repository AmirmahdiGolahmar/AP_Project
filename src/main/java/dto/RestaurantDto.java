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
        if(restaurant.getId() != null) this.id = restaurant.getId();
        if(restaurant.getName() != null) this.name = restaurant.getName();
        if(restaurant.getAddress() != null) this.address = restaurant.getAddress();
        if(restaurant.getPhone() != null) this.phone = restaurant.getPhone();
        if (restaurant.getLogo() != null) this.logoBase64 = restaurant.getLogo();
        if(restaurant.getTaxFee() != null) this.tax_fee = restaurant.getTaxFee();
        if(restaurant.getAdditionalFee() != null) this.additional_fee = restaurant.getAdditionalFee();
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
