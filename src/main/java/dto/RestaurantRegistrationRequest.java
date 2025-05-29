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
    Integer taxFee;
    Integer additionalFee;
}
