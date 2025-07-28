package dto;

import entity.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemSellerViewDto {
    long id;
    String name;
    String photo;
    String description;
    Double price;
    Integer supply;
    List<String> keywords;
    Double rating;

    public ItemSellerViewDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        if(item.getPhoto() != null) this.photo = item.getPhoto();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.supply = item.getSupply();
        this.keywords = item.getKeywords();
        this.rating = item.getRating();
    }
}
