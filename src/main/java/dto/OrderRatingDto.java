package dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRatingDto {
    Long order_id;
    int rating;
    String comment;
    List<String> imageBase64;
}
