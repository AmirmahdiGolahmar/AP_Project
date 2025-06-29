package service;

import dao.GenericDao;
import dao.ItemDao;
import dao.RestaurantDao;
import dto.itemDto;
import entity.Item;
import entity.Restaurant;
import exception.AlreadyExistsException;
import exception.ForbiddenException;
import exception.NotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.Optional;

public class ItemService {
    private final ItemDao itemDao;
    private final RestaurantDao restaurantDao;

    public ItemService() {
        this.itemDao = new ItemDao();
        this.restaurantDao = new RestaurantDao();
    }

    public itemDto addItemToRestaurant(Long restaurantId, itemDto itemRequest) {

        // Find the restaurant
        Restaurant restaurant = restaurantDao.findById(restaurantId);

        Optional<Item> it = restaurant.getItems()
                .stream()
                .filter(i -> i.getName().equals(itemRequest.getName()))
                .findFirst();

        if(!it.isEmpty()) throw new AlreadyExistsException("Item with this name already exists");

        // Create new item
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setPhoto(itemRequest.getImageBase64());
        item.setDescription(itemRequest.getDescription());
        item.setPrice(itemRequest.getPrice());
        item.setSupply(itemRequest.getSupply());
        item.setRating(0.0); // Default rating
        item.setKeywords(itemRequest.getKeywords() != null ? itemRequest.getKeywords() : new ArrayList<>());

        // Save the item
        itemDao.save(item);
        // Add item to restaurant using the addItem method
        restaurant.addItem(item);
        restaurantDao.update(restaurant);

        // Return the created item as DTO
        itemDto response = new itemDto();
        response.setName(item.getName());
        response.setImageBase64(item.getPhoto());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setSupply(item.getSupply());
        response.setKeywords(item.getKeywords());
        
        return response;
    }

    public itemDto editItem(Long restaurantId, Long itemId, itemDto request, Long userId) {
        // Validate
        Restaurant restaurant = restaurantDao.findById(restaurantId);


        Item item = restaurant.getItems()
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item not found in restaurant"));

        // Edit item
        if (request.getName() != null) item.setName(request.getName());
        if (request.getImageBase64() != null) item.setPhoto(request.getImageBase64());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getSupply() != null) item.setSupply(request.getSupply());
        if (request.getKeywords() != null) item.setKeywords(new ArrayList<>(request.getKeywords()));

        itemDao.update(item);

        return  new itemDto(item);
    }

    public void deleteItem(Long restaurantId, Long itemId) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);

        Item item = restaurant.getItems()
                .stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item not found in this restaurant"));

        restaurant.getItems().remove(item);
        itemDao.delete(item.getId());

        restaurantDao.save(restaurant);
    }

    // Future methods can be added here:
    // - updateItem()
    // - deleteItem()
    // - findItemsByRestaurant()
    // - findItemById()
    // etc.
} 