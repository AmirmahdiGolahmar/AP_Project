package service;

import dao.ItemDao;
import dao.RestaurantDao;
import dto.itemDto;
import entity.Item;
import entity.Restaurant;
import exception.ForbiddenException;
import exception.NotFoundException;

import java.util.ArrayList;

public class ItemService {
    private final ItemDao itemDao;
    private final RestaurantDao restaurantDao;

    public ItemService() {
        this.itemDao = new ItemDao();
        this.restaurantDao = new RestaurantDao();
    }

    public itemDto addItemToRestaurant(Long restaurantId, itemDto itemRequest) {
        // Validate input
        if (itemRequest.getName() == null || itemRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (itemRequest.getPrice() == null || itemRequest.getPrice() <= 0) {
            throw new IllegalArgumentException("Item price must be positive");
        }
        if (itemRequest.getSupply() == null || itemRequest.getSupply() < 0) {
            throw new IllegalArgumentException("Item supply cannot be negative");
        }

        // Find the restaurant
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }

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
        if (restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }
        if (!restaurant.getSeller().getId().equals(userId)) {
            throw new ForbiddenException("You are not allowed to edit items of this restaurant");
        }

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



    // Future methods can be added here:
    // - updateItem()
    // - deleteItem()
    // - findItemsByRestaurant()
    // - findItemById()
    // etc.
} 