package service;

import dao.*;
import dto.*;
import entity.*;
import exception.*;
import util.SearchUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerService {
    private final ItemDao itemDao;
    private final RestaurantDao restaurantDao;
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final CouponDao couponDao;
    private final ItemRatingDao itemRatingDao;

    public CustomerService() {
        itemDao = new ItemDao();
        restaurantDao = new RestaurantDao();
        userDao = new UserDao();
        orderDao = new OrderDao();
        couponDao = new CouponDao();
        itemRatingDao = new ItemRatingDao();
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

    public OrderDto addOrder(OrderRegistrationRequest request, long customerId) {
        Customer customer = (Customer) userDao.findById(customerId);
        Restaurant restaurant = restaurantDao.findById(request.getVendor_id());
        if(request.getItems().isEmpty()) throw new InvalidInputException("Cart can't be empty");
        List<CartItem> cartItems = new ArrayList<>();
        for(CartItemDto ci : request.getItems()){
          Optional<Item> it = restaurant.getItems().stream().filter(itm -> itm.getId() == ci.getItem_id()).findFirst();
            if(it.isEmpty()) throw new NotFoundException("Item doesn't exist");
            Item item = it.get();
            if(item.getSupply() < ci.getQuantity()) throw new InvalidInputException("Supply isn't enough");
            item.subtractSupplyCount(ci.getQuantity());
            cartItems.add(new CartItem(item, ci.getQuantity()));
            itemDao.update(item);
        }

        Order order;
        Coupon coupon = null;
        if(request.getCoupon_id() == null){
            order = new Order(cartItems, request.getDelivery_address(),
                    customer, restaurant, LocalDateTime.now(), LocalDateTime.now(), OrderStatus.submitted);
        }else{
            coupon = couponDao.findById(request.getCoupon_id());
            if(coupon == null) throw new NotFoundException("Coupon doesn't exist");
            coupon.subtractUserCount();
            couponDao.update(coupon);
            order = new Order(cartItems, request.getDelivery_address(),
                    customer, restaurant, LocalDateTime.now(), LocalDateTime.now(), coupon, OrderStatus.submitted);
        }

        orderDao.save(order);

        return new OrderDto(order);
    }

    public OrderDto getOrder(long orderId) {
        Order order = orderDao.findById(orderId);
        if(order == null) throw new NotFoundException("Order doesn't exist");
        return new OrderDto(order);
    }

    public List<OrderDto> searchOrderHistory(String search, String vendor, Long userId){
        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String vendorFilter = (vendor == null || vendor.isBlank()) ? "" : vendor.toLowerCase();

        List<Order> allOrders = orderDao.findAll().stream()
                .filter(o -> o.getCustomer().getId().equals(userId)).collect(Collectors.toList());

        List<String> searchFields = List.of("deliveryAddress", "coupon.code", "status",
                "delivery.fullName", "restaurant.name", "createdAt", "updatedAt");

        Map<String, String> filters = new HashMap<>();
        if (!vendorFilter.isBlank()) filters.put("restaurant.name", vendorFilter);

        List<Order> result =
                SearchUtil.search(allOrders, Order.class, searchFilter, searchFields, filters);

        return result.stream().map(OrderDto::new).toList();
    }

    public void addToFavorites(long userId, long restaurantId) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        User user = userDao.findByIdLoadFavorites(userId);
        user.addToFavorite(restaurant);
        userDao.update(user);
    }

    public void removeFromFavorites(long userId, long restaurantId) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        User user = userDao.findByIdLoadFavorites(userId);
        user.removeFromFavorite(restaurant);
        userDao.update(user);
    }

    public List<RestaurantDto> getFavorites(long userId) {
        User user = userDao.findByIdLoadFavorites(userId);
        return user.getFavoriteRestaurants().stream().map(RestaurantDto::new).collect(Collectors.toList());
    }

    public void submitOrderRating(OrderRatingDto request, long userId) {
        if (request == null || request.getOrder_id() == null || request.getRating() < 0 || request.getRating() > 5) {
            throw new InvalidInputException("Invalid rating request");
        }
        Order order = orderDao.findById(request.getOrder_id());
        if (order == null) {
            throw new NotFoundException("Order not found");
        }
        if (order.getRating() != null) {
            throw new AlreadyExistsException("Order already rated");
        }

        User user = userDao.findById(userId);

        //Submit order rating
        OrderRating rating = new OrderRating();
        rating.setComment(request.getComment());
        rating.setImageBase64(request.getImageBase64());
        rating.setRating(request.getRating());
        rating.setOrder(orderDao.findById(request.getOrder_id()));
        rating.setCreatedAt(LocalDateTime.now());

        order.setRating(rating);

        //Submit item rating 
        for(CartItem cartItem : order.getCartItems()){
            Item item = cartItem.getItem();
            ItemRating itemRating = new ItemRating();
            itemRating.setRating(request.getRating());
            itemRating.setComment(request.getComment());
            itemRating.setImageBase64(request.getImageBase64());
            itemRating.setCreatedAt(LocalDateTime.now());
            itemRating.setItem(item);
            itemRating.setUser(user);

            itemRatingDao.save(itemRating);
        }

        orderDao.update(order);
    }

    public ItemRatingAvgResponseDto getItemAvgRating(Long itemId){
        Item item = itemDao.findById(itemId);
        if(item == null) throw new NotFoundException("This item doesn't exist");

        List<ItemRatingResponseDto> itemsRating = itemRatingDao.findAll().stream()
            .filter(i -> i.getItem().getId().equals(itemId))
            .map(ItemRatingResponseDto::new).collect(Collectors.toList());

        ItemRatingAvgResponseDto response = new ItemRatingAvgResponseDto();
        response.setAvg_rating( itemsRating.stream()
            .mapToInt(ItemRatingResponseDto::getRating)
            .average()
            .orElse(0));
        response.setComments(itemsRating);

        return response;
    }

    public ItemRatingResponseDto getItemRating(Long itemId){
        ItemRating itemRating = itemRatingDao.findById(itemId);
        if(itemRating == null) throw new NotFoundException("This rating doesn't exist");
        return new ItemRatingResponseDto(itemRating);
    }

    public void deleteRating(Long itemId) {
        ItemRating itemRatings = itemRatingDao.findById(itemId);
        if(itemRatings == null) throw new NotFoundException("This item doesn't exist");
        itemRatingDao.delete(itemId);
    }

    public void updateItemRating(ItemRatingRequestDto request, long userId, long ratingId) {
        if(request.getRating() == null || request.getComment() == null){
            throw new InvalidInputException("rating and comment can not be empty");
        }

        ItemRating itemRating = itemRatingDao.findById(ratingId);
        if(itemRating == null) throw new NotFoundException("This rating doesn't exist");

        if(itemRating.getUser().getId() != userId) 
            throw new UnauthorizedUserException("You can't edit this rating");
        
        itemRating.setRating(request.getRating());
        itemRating.setComment(request.getComment());
        itemRating.setImageBase64(request.getImageBase64());

        itemRatingDao.update(itemRating);
    }


}