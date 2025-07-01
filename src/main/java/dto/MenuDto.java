package dto;

import entity.Item;
import entity.Menu;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class MenuDto {
    String title;
    List<ItemDto> items;

    public MenuDto(Menu menu) {
        this.title = menu.getTitle();
        this.items = menu.getItems().stream().map(ItemDto::new).collect(Collectors.toList());
    }
}
