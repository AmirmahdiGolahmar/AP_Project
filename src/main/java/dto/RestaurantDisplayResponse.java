package dto;

import entity.Item;
import entity.Restaurant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestaurantDisplayResponse {
    List<String> menu_titles;
    List<MenuDto> menus;

}
