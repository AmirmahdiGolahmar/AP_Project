package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantUpdateRequest {
    private String name;
    private String address;
    private String phone;
    private String logoBase64;
    private Integer tax_fee;
    private Integer additional_fee;

    public RestaurantUpdateRequest() {
    }

}
