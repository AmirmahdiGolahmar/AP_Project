package dto;


import lombok.Getter;

@Getter
public class RestaurantRegistrationRequest {
    String name;
    String address;
    String phone;
    String logoBase64;
    Double tax_fee;
    Double additional_fee;
}
