package dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRatingAvgResponseDto {
    Double avg_rating;
    List<ItemRatingResponseDto> comments;
}
