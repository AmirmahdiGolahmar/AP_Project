package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestaurantSearchRequestDto {
    String search;
    List<String> keywords;
}
