package service;

import dao.*;
import dto.*;
import entity.*;
import exception.InvalidInputException;
import exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.LocalDateTimeAdapter.TimeToString;

public class CustomerService {
    private final ItemDao itemDao;
    private final RestaurantDao restaurantDao;
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final CouponDao couponDao;

    public CustomerService() {
        itemDao = new ItemDao();
        restaurantDao = new RestaurantDao();
        userDao = new UserDao();
        orderDao = new OrderDao();
        couponDao = new CouponDao();
    }

    public List<RestaurantDto> searchRestaurant(RestaurantSearchRequestDto request) {
        List<Restaurant> restaurants = restaurantDao.getAllRestaurants();

        String search = request.getSearch() == null ? "" : request.getSearch().toLowerCase();
        List<String> keywords = request.getKeywords() == null ? List.of() :
                request.getKeywords().stream().map(String::toLowerCase).toList();

        if (search.isEmpty() && keywords.isEmpty()) {
            return restaurants.stream().map(RestaurantDto::new).collect(Collectors.toList());
        }

        return restaurants.stream()
                .filter(restaurant ->
                        restaurant.getName().toLowerCase().contains(search) ||
                                restaurant.getAddress().toLowerCase().contains(search) ||
                                restaurant.getSeller().getFullName().toLowerCase().contains(search) ||
                                restaurant.getPhone().contains(search) ||
                                restaurant.getMenus().stream()
                                        .anyMatch(mn -> mn.getTitle().toLowerCase().contains(search)) ||

                                keywords.stream().anyMatch(keyword ->
                                        restaurant.getName().toLowerCase().contains(keyword) ||
                                                restaurant.getAddress().toLowerCase().contains(keyword) ||
                                                restaurant.getSeller().getFullName().toLowerCase().contains(keyword)) ||

                                restaurant.getMenus().stream()
                                        .anyMatch(menu -> keywords.stream()
                                                .anyMatch(keyword -> menu.getTitle().toLowerCase().contains(keyword)))
                )
                .map(RestaurantDto::new)
                .toList();
    }


    public RestaurantDisplayResponse displayRestaurant(long restaurantId) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        RestaurantDisplayResponse response = new RestaurantDisplayResponse();
        response.setVendor(new RestaurantDto(restaurant));
        response.setMenu_titles(restaurant.getMenus().stream().map(Menu::getTitle).collect(Collectors.toList()));
        response.setMenus(restaurant.getMenus().stream().map(MenuDto::new).collect(Collectors.toList()));
        return response;
    }

    public List<ItemDto> searchItem(ItemSearchRequestDto request) {
        List<Item> items = itemDao.findAll();

        if ((request.getSearch() == null || request.getSearch().isEmpty()) &&
                (request.getKeywords() == null || request.getKeywords().isEmpty()) &&
                request.getPrice() == 0) {
            return items.stream().map(ItemDto::new).collect(Collectors.toList());
        }

        String search = request.getSearch() == null ? "" : request.getSearch().toLowerCase();
        List<String> keywords = request.getKeywords() == null ? List.of() :
                request.getKeywords().stream().map(String::toLowerCase).toList();
        double price = request.getPrice();

        return items.stream()
                .filter(i -> i.getName().toLowerCase().contains(search) ||
                        i.getKeywords().stream().anyMatch(keyword -> keyword.toLowerCase().contains(search)) ||
                        i.getPrice() == price ||
                        keywords.stream().anyMatch(keyword -> i.getName().toLowerCase().contains(keyword)) ||
                        keywords.stream().anyMatch(keyword -> i.getKeywords().stream().anyMatch(word -> word.toLowerCase().contains(keyword)))
                )
                .map(ItemDto::new)
                .collect(Collectors.toList());
    }


    public ItemDto displayItem(long itemId){
        Item item = itemDao.findById(itemId);
        if(item == null) throw new NotFoundException("Item not found");
        return new ItemDto(item);
    }

    public CouponDto getCoupon(String code) {
        Coupon coupon = couponDao.findAll().stream().filter(cp -> cp.getCode().equals(code)).findFirst().orElse(null);
        if(coupon == null) throw new NotFoundException("Coupon doesn't exist");
        return new CouponDto(coupon);
    }

    //delivery id
    public OrderDto addOrder(OrderRegistrationRequest request, long customerId) {
        Customer customer = (Customer) userDao.findById(customerId);
        Restaurant restaurant = restaurantDao.findById(request.getVendor_id());
        List<CartItem> cartItems = new ArrayList<>();
        for(CartItemDto ci : request.getItems()){
            Item item = restaurant.getItems().stream().filter(itm -> itm.getId() == ci.getItem_id()).findFirst().get();
            if(item == null) throw new NotFoundException("Item doesn't exist");
            if(item.getSupply() < ci.getQuantity()) throw new InvalidInputException("Supply isn't enough");
            cartItems.add(new CartItem(item, ci.getQuantity()));
        }

        Order order;
        Coupon coupon = null;
        if(request.getCoupon_id() == null){
            order = new Order(cartItems, request.getDelivery_address(),
                    customer, restaurant, LocalDateTime.now(), LocalDateTime.now(), OrderStatus.submitted);
        }else{
            coupon = couponDao.findById(request.getCoupon_id());
            order = new Order(cartItems, request.getDelivery_address(),
                    customer, restaurant, LocalDateTime.now(), LocalDateTime.now(), coupon, OrderStatus.submitted);
        }

        orderDao.save(order);

        if(coupon != null){
            coupon.subtractUserCount();
            couponDao.update(coupon);
        }

        return new OrderDto(order);
    }

    public OrderDto getOrder(long orderId) {
        Order order = orderDao.findById(orderId);
        if(order == null) throw new NotFoundException("Order doesn't exist");
        return new OrderDto(order);
    }
}
