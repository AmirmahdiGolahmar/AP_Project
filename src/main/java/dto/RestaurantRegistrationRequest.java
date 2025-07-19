package dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestaurantRegistrationRequest {
    String name;
    String address;
    String phone;
    String logoBase64;
    Double tax_fee;
    Double additional_fee;
}
