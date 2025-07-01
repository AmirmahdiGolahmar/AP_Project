package service;

import dao.ItemDao;
import dao.RestaurantDao;
import dto.*;
import entity.Item;
import entity.Menu;
import entity.Restaurant;
import exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    private final ItemDao itemDao;
    private final RestaurantDao restaurantDao;

    public CustomerService() {
        itemDao = new ItemDao();
        restaurantDao = new RestaurantDao();
    }

    public List<RestaurantDto> searchRestaurant(RestaurantSearchRequestDto request) {
        String search = request.getSearch().toLowerCase();
        List<String> keywords = request.getKeywords().stream().map(String::toLowerCase).toList();
        List<Restaurant> restaurants = restaurantDao.getAllRestaurants();
        if (search.isEmpty() && keywords.isEmpty()){
            return restaurants.stream().map(RestaurantDto::new).collect(Collectors.toList());
        }

        return restaurants.stream()
                .filter(restaurant ->
                        restaurant.getName().toLowerCase().contains(search) ||
                                restaurant.getAddress().toLowerCase().contains(search) ||
                                restaurant.getSeller().getFullName().toLowerCase().contains(search) ||
                                restaurant.getPhone().contains(search) ||
                                restaurant.getMenus().stream().anyMatch(mn -> mn.getTitle().toLowerCase().contains(search)) ||
                                keywords.stream().anyMatch(keyword -> restaurant.getName().toLowerCase().contains(keyword)) ||
                                keywords.stream().anyMatch(keyword -> restaurant.getAddress().toLowerCase().contains(keyword)) ||
                                keywords.stream().anyMatch(keyword -> restaurant.getSeller().getFullName().toLowerCase().contains(keyword)) ||
                                restaurant.getMenus().stream()
                                        .anyMatch(menu -> keywords.stream().anyMatch(keyword -> menu.getTitle().toLowerCase().contains(keyword)))

                ).map(RestaurantDto::new).toList();
    }

    public RestaurantDisplayResponse displayRestaurant(long restaurantId) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        RestaurantDisplayResponse response = new RestaurantDisplayResponse();
        response.setMenu_titles(restaurant.getMenus().stream().map(Menu::getTitle).collect(Collectors.toList()));
        response.setMenus(restaurant.getMenus().stream().map(MenuDto::new).collect(Collectors.toList()));
        return response;
    }

    public List<ItemDto> searchItem(ItemSearchRequestDto request) {
        String search = request.getSearch().toLowerCase();
        List<String> keywords = request.getKeywords().stream().map(String::toLowerCase).toList();
        double price = request.getPrice();

        List<Item> items = itemDao.findAll();
        if (search.isEmpty() && keywords.isEmpty() && price == 0){
            return items.stream().map(ItemDto::new).collect(Collectors.toList());
        }

        return items.stream()
                .filter(i -> i.getName().toLowerCase().contains(search) ||
                        i.getKeywords().stream().anyMatch(keyword -> keyword.toLowerCase().contains(search)) ||
                        i.getPrice() == price ||
                        keywords.stream().anyMatch(keyword -> i.getName().toLowerCase().contains(keyword)) ||
                        keywords.stream().anyMatch(keyword -> i.getKeywords().stream().anyMatch(word -> word.toLowerCase().contains(keyword)))
                ).map(ItemDto::new).collect(Collectors.toList());

    }

    public ItemDto displayItem(long itemId){
        Item item = itemDao.findById(itemId);
        if(item == null) throw new NotFoundException("Item not found");
        return new ItemDto(item);
    }
}
