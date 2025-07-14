package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliverySearchRequestDto {
    String search;
    String vendor;
    String user;
}
