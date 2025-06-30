package dto;

import entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class menuDto {
    String title;
    List<Item> items;
}
