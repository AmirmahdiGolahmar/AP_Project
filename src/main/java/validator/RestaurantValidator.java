package validator;

import dto.itemDto;
import entity.Item;
import entity.Restaurant;
import exception.InvalidInputException;

public class RestaurantValidator {
    public static void itemValidator(itemDto item) {
        if(item.getName() == null || item.getName().equals("")) throw new InvalidInputException("Invalid name");
        if(item.getPrice() < 0) throw new InvalidInputException("Invalid price");
        if(item.getDescription() == null || item.getDescription().equals("")) throw new InvalidInputException("Invalid description");
        if(item.getSupply() < 0) throw new InvalidInputException("Invalid supply");
        if(item.getImageBase64() == null || item.getImageBase64().equals("")) throw new InvalidInputException("Invalid image");
        if(item.getKeywords().isEmpty()) throw new InvalidInputException("Invalid keywords");
    }
}
