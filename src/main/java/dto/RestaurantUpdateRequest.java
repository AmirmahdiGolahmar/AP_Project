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
    private Integer taxFee;
    private Integer additionalFee;

    public RestaurantUpdateRequest() {
    }

}
