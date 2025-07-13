package dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRatingRequestDto {
    Integer rating;
    String comment;
    List<String> imageBase64;
}
