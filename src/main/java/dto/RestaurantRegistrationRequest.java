package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRegistrationRequest {
    String name;
    String address;
    String phone;
    String logoBase64;
    Integer tax_fee;
    Integer additional_fee;
}
