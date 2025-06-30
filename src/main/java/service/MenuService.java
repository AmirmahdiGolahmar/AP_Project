package service;

import dao.ItemDao;
import dao.MenuDao;
import dao.RestaurantDao;
import dto.menuDto;
import entity.Menu;
import entity.Restaurant;
import exception.AlreadyExistsException;
import exception.NotFoundException;

import java.io.NotActiveException;
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

    public Menu addMenu(menuDto request, long restaurantId){
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
}
