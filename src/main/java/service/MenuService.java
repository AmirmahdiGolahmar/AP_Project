package service;

import dao.ItemDao;
import dao.MenuDao;
import dao.RestaurantDao;
import dto.ItemDto;
import dto.MenuDto;
import entity.Item;
import entity.Menu;
import entity.Restaurant;
import exception.AlreadyExistsException;
import exception.NotFoundException;

import java.util.Optional;

public class MenuService {
    private final ItemDao itemDao;
    private final MenuDao menuDao;
    private final RestaurantDao restaurantDao;

    public MenuService(){
        restaurantDao = new RestaurantDao();
        itemDao = new ItemDao();
        menuDao = new MenuDao();
    }

    public Menu addMenu(MenuDto request, long restaurantId){
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        String title = request.getTitle();
        Optional<Menu> mn = restaurant.getMenus().stream()
                .filter(m -> m.getTitle().equals(title)).findFirst();
        if(!mn.isEmpty()){throw new AlreadyExistsException("This menu already exists");}

        Menu menu = new Menu(title, restaurant);
        restaurant.addMenu(menu);
        restaurantDao.update(restaurant);
        return menu;
    }

    public void deleteMenu(String menuTitle, long restaurantId){
        Restaurant restaurant = restaurantDao.findById(restaurantId);

        Optional<Menu> menu = restaurant.getMenus().stream()
                .filter(m -> m.getTitle().equals(menuTitle)).findFirst();

        if(menu.isEmpty()){throw new NotFoundException("This menu does not exist");}

        restaurant.removeMenu(menu.get());

        restaurantDao.update(restaurant);
    }

    public void addItem(String title, int itemId, long restaurantId){
        Restaurant restaurant = restaurantDao.findById(restaurantId);

        Menu menu = restaurant.getMenus().stream().filter(m -> m.getTitle().equals(title)).findFirst().orElse(null);
        if(menu == null) throw new NotFoundException("This menu does not exist");

        Item item = restaurant.getItems().stream().filter(i -> i.getId() == itemId).findFirst().orElse(null);
        if(item == null){throw new NotFoundException("This item does not exist");}

        if(menu.getItems().stream().anyMatch(itm -> itm.getId() == itemId))
            throw new AlreadyExistsException("This item already exists");

        menu.addItem(item);

        menuDao.update(menu);
        restaurantDao.update(restaurant);
    }

    public void deleteItem(String title, int itemId, long restaurantId){
        Restaurant restaurant = restaurantDao.findById(restaurantId);

        Menu menu = restaurant.getMenus().stream().filter(m -> m.getTitle().equals(title)).findFirst().orElse(null);
        if(menu == null) throw new NotFoundException("This menu does not exist");

        Item item = menu.getItems().stream().filter(i -> i.getId() == itemId).findFirst().orElse(null);
        if(item == null){throw new NotFoundException("This item does not exist");}

        menu.removeItem(item);
        menuDao.update(menu);
        restaurantDao.update(restaurant);
    }
}
