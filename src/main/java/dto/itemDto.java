package dto;

import entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class itemDto {
    String name;
    String imageBase64;
    String description;
    Double price;
    Integer supply;
    List<String> keywords;

    public itemDto(String name, String imageBase64, String description, Double price, Integer supply, List<String> keywords) {
        this.name = name;
        this.imageBase64 = imageBase64;
        this.description = description;
        this.price = price;
        this.supply = supply;
        this.keywords = keywords;
    }

    public itemDto(Item item) {
        this.name = item.getName();
        this.imageBase64 = item.getPhoto();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.supply = item.getSupply();
        this.keywords = item.getKeywords();
    }

    public itemDto() {

    }

}


