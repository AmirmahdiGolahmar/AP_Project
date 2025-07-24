package service;

import dao.ItemDao;
import dao.RestaurantDao;
import dto.ItemDto;
import entity.Item;
import entity.Restaurant;
import exception.AlreadyExistsException;
import exception.NotFoundException;

import java.util.ArrayList;
import java.util.Optional;
import static util.validator.validator.*;

public class ItemService {
    private final ItemDao itemDao;
    private final RestaurantDao restaurantDao;

    public ItemService() {
        this.itemDao = new ItemDao();
        this.restaurantDao = new RestaurantDao();
    }

    public ItemDto addItem(Restaurant restaurant, ItemDto request) {

        Optional<Item> it = restaurant.getItems()
                .stream()
                .filter(i -> i.getName().equals(request.getName()))
                .findFirst();

        if(it.isPresent()) throw new AlreadyExistsException("Item with this name already exists");

        Item item = new Item(request.getName(), request.getPhoto(), request.getDescription(),
                        request.getPrice(), request.getSupply(), request.getKeywords(), restaurant, 0.0);

        restaurant.addItem(item);
        restaurantDao.update(restaurant);

        return new ItemDto(item);
    }

    public ItemDto editItem(Restaurant restaurant, Item item, ItemDto request) {


        Item it = restaurant.getItems()
                .stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item not found in restaurant"));

        if (request.getName() != null) it.setName(request.getName());
        if (request.getPhoto() != null) it.setPhoto(request.getPhoto());
        if (request.getDescription() != null) it.setDescription(request.getDescription());
        if (request.getPrice() != null){
            priceValidator(request.getPrice());
            it.setPrice(request.getPrice());
        }
        if (request.getSupply() != null){
            supplyValidator(request.getSupply());
            it.setSupply(request.getSupply());
        }
        if (request.getKeywords() != null) it.setKeywords(new ArrayList<>(request.getKeywords()));

        itemDao.update(it);

        return  new ItemDto(it);
    }

    public void deleteItem(Restaurant restaurant, Item item) {
        Item it = restaurant.getItems()
                .stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item doesn't exist"));

        restaurant.removeItem(it);

        restaurantDao.update(restaurant);
    }
} 