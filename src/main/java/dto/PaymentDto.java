package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDto {
    Long order_id;
    String method;
}
