package dto;

import static util.LocalDateTimeAdapter.TimeToString;

import java.util.List;

import entity.ItemRating;
import lombok.Getter;
import lombok.Setter;
import util.LocalDateTimeAdapter;


@Getter
@Setter
public class ItemRatingResponseDto {
    Long id;
    Long item_id;
    Integer rating;
    String comment;
    List<String> imageBase64;
    Long userId;
    String created_at;

    public ItemRatingResponseDto(ItemRating itemRating){
        this.id = itemRating.getId();
        this.item_id = itemRating.getItem().getId();
        this.rating = itemRating.getRating();
        this.comment = itemRating.getComment();
        this.imageBase64 = itemRating.getImageBase64();
        this.userId = itemRating.getUser().getId();
        this.created_at = TimeToString(itemRating.getCreatedAt());
    }
}
