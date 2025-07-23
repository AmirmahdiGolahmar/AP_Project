package service;

import dao.ItemDao;
import dao.MenuDao;
import dao.RestaurantDao;
import dto.ItemAddToMenuRequestDto;
import dto.MenuDto;
import dto.MenuRegistrationDto;
import entity.Item;
import entity.Menu;
import entity.Restaurant;
import exception.AlreadyExistsException;
import exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuService {
    private final ItemDao itemDao;
    private final MenuDao menuDao;
    private final RestaurantDao restaurantDao;

    public MenuService(){
        restaurantDao = new RestaurantDao();
        itemDao = new ItemDao();
        menuDao = new MenuDao();
    }

    public Menu addMenu(MenuRegistrationDto request, Restaurant restaurant){
        String title = request.getTitle();
        Optional<Menu> mn = restaurant.getMenus().stream()
                .filter(m -> m.getTitle().equals(title)).findFirst();
        if(mn.isPresent()){throw new AlreadyExistsException("This menu already exists");}

        Menu menu = new Menu(title, restaurant);
        restaurant.addMenu(menu);
        restaurantDao.update(restaurant);
        return menu;
    }

    public void deleteMenu(String menuTitle, Restaurant restaurant){

        Optional<Menu> menu = restaurant.getMenus().stream()
                .filter(m -> m.getTitle().equals(menuTitle)).findFirst();

        if(menu.isEmpty()){throw new NotFoundException("This menu does not exist");}

        restaurant.removeMenu(menu.get());

        restaurantDao.update(restaurant);
    }

    public void addItem(String title, Restaurant restaurant, ItemAddToMenuRequestDto request){

        Menu menu = restaurant.getMenus().stream().filter(m -> m.getTitle().equalsIgnoreCase(title))
                .findFirst().orElseThrow(() -> new NotFoundException("This menu does not exist"));

        Item it = restaurant.getItems().stream().filter(i -> i.getId() == request.getItem_id())
                .findFirst().orElseThrow(() -> new NotFoundException("This item does not exist"));

        if(menu.getItems().stream().anyMatch(itm -> itm.getId() == it.getId()))
            throw new AlreadyExistsException("This item already exists in this menu");

        menu.addItem(it);

        menuDao.update(menu);
        restaurantDao.update(restaurant);
    }

    public void deleteItem(String menuTile, Item item, Restaurant restaurant){
        List<Menu> menus = restaurant.getMenus();
        Menu menu = menus.stream().filter(m -> m.getTitle().equalsIgnoreCase(menuTile))
                .findFirst().orElseThrow(() -> new NotFoundException("This menu does not exist"));

        Item it = menu.getItems().stream().filter(i -> i.getId() == item.getId()).
                findFirst().orElseThrow(() -> new NotFoundException("This item does not exist in this menu"));

        menu.removeItem(it);
        menuDao.update(menu);
        restaurantDao.update(restaurant);
    }

    public List<MenuDto> getRestaurantMenus(Restaurant restaurant) {
        return menuDao.findAll().stream()
                .filter(m-> m.getRestaurant().getId().equals(restaurant.getId())).map(MenuDto::new).toList();
    }

    public MenuDto getRestaurantMenu(Restaurant restaurant, String menuTitle) {
        return menuDao.findAll().stream().filter(
                m -> m.getRestaurant().getId().equals(restaurant.getId()) && m.getTitle().equalsIgnoreCase(menuTitle)
        ).map(MenuDto::new).toList().get(0);
    }
}
