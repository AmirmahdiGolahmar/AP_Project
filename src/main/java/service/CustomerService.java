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
    private final CustomerDao customerDao;
    private final CartItemDao cartItemDao;

    public CustomerService() {
        itemDao = new ItemDao();
        restaurantDao = new RestaurantDao();
        userDao = new UserDao();
        orderDao = new OrderDao();
        couponDao = new CouponDao();
        itemRatingDao = new ItemRatingDao();
        customerDao = new CustomerDao();
        cartItemDao = new CartItemDao();
    }

    public List<RestaurantDto> searchRestaurant(RestaurantSearchRequestDto request) {
        List<Restaurant> restaurants = restaurantDao.getAllRestaurants();

        if (request == null)
            return restaurants.stream().map(RestaurantDto::new).toList();

        String search = Optional.ofNullable(request.getSearch())
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .orElse(null);

        List<String> keywords = Optional.ofNullable(request.getKeywords())
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(String::toLowerCase)
                        .toList())
                .orElse(null);

        if (search == null && (keywords == null || keywords.isEmpty()))
            return restaurants.stream().map(RestaurantDto::new).toList();

        return restaurants.stream()
                .filter(restaurant -> {
                    boolean matchesSearch = search != null && (
                            restaurant.getName().toLowerCase().contains(search) ||
                                    restaurant.getAddress().toLowerCase().contains(search) ||
                                    restaurant.getSeller().getFullName().toLowerCase().contains(search) ||
                                    restaurant.getPhone().contains(search) ||
                                    restaurant.getMenus().stream()
                                            .anyMatch(m -> m.getTitle().toLowerCase().contains(search))
                    );

                    boolean matchesKeywords = keywords != null && (
                            keywords.stream().anyMatch(keyword ->
                                    restaurant.getName().toLowerCase().contains(keyword) ||
                                            restaurant.getAddress().toLowerCase().contains(keyword) ||
                                            restaurant.getSeller().getFullName().toLowerCase().contains(keyword)) ||

                                    restaurant.getMenus().stream()
                                            .anyMatch(menu -> keywords.stream()
                                                    .anyMatch(keyword -> menu.getTitle().toLowerCase().contains(keyword)))
                    );

                    return matchesSearch || matchesKeywords;
                })
                .map(RestaurantDto::new)
                .toList();
    }



    public RestaurantDisplayResponse displayRestaurant(Restaurant restaurant) {
        RestaurantDisplayResponse response = new RestaurantDisplayResponse();
        response.setVendor(new RestaurantDto(restaurant));
        response.setMenu_titles(restaurant.getMenus().stream().map(Menu::getTitle).collect(Collectors.toList()));
        response.setMenus(restaurant.getMenus().stream().map(MenuDto::new).collect(Collectors.toList()));
        return response;
    }

    public List<ItemDto> searchItem(ItemSearchRequestDto request) {
        List<Item> items = itemDao.findAll();

        if (request == null) {
            return items.stream().map(ItemDto::new).toList();
        }

        String search = Optional.ofNullable(request.getSearch())
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .orElse(null);

        List<String> keywords = Optional.ofNullable(request.getKeywords())
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(String::toLowerCase)
                        .toList())
                .orElse(null);

        Long price = request.getPrice() == null ?  null : request.getPrice();

        if (search == null && (keywords == null || keywords.isEmpty()) && price == null) {
            return items.stream().map(ItemDto::new).toList();
        }

        return items.stream()
                .filter(item -> {
                    boolean matchSearch = search != null && (
                            item.getName().toLowerCase().contains(search) ||
                                    item.getKeywords().stream()
                                            .filter(Objects::nonNull)
                                            .anyMatch(k -> k.toLowerCase().contains(search))
                    );

                    boolean matchKeywords = keywords != null && (
                            keywords.stream().anyMatch(kw -> item.getName().toLowerCase().contains(kw)) ||
                                    keywords.stream().anyMatch(kw ->
                                            item.getKeywords().stream()
                                                    .filter(Objects::nonNull)
                                                    .anyMatch(w -> w.toLowerCase().contains(kw))
                                    )
                    );

                    boolean matchPrice = price != null && item.getPrice() == price;

                    return matchSearch || matchKeywords || matchPrice;
                })
                .map(ItemDto::new)
                .toList();
    }



    public ItemDto displayItem(Item item){
        return new ItemDto(item);
    }

    public CouponDto getCoupon(String code) {
        Coupon coupon = couponDao.findAll().stream().filter(cp -> cp.getCode().equals(code)).findFirst().orElse(null);
        if(coupon == null) throw new NotFoundException("Coupon doesn't exist");
        return new CouponDto(coupon);
    }

    public OrderDto addOrder(OrderRegistrationRequest request, Customer customer, Restaurant restaurant, Coupon coupon) {
        List<CartItem> cartItems = new ArrayList<>();
        for(CartItemDto ci : request.getItems()){
          Optional<Item> it = restaurant.getItems().stream().filter(itm -> itm.getId() == ci.getItem_id()).findFirst();
            if(it.isEmpty()) throw new NotFoundException("Item doesn't exist");
            Item item = it.get();
            if(item.getSupply() < ci.getQuantity()) throw new InvalidInputException("Supply isn't enough");
            if(ci.getQuantity() > 0){
                //item.subtractSupplyCount(ci.getQuantity());
                CartItem cartItem = new CartItem();
                cartItem.setQuantity(ci.getQuantity());
                cartItem.setItem(item);
                cartItem.setUser(customer);
                cartItems.add(cartItem);
                itemDao.update(item);
            }
        }

        Order order;
        if(coupon == null){
            order = new Order(cartItems, request.getDelivery_address(),
                    customer, restaurant, LocalDateTime.now(), LocalDateTime.now(), OrderStatus.unpaid);
        }else{
            //coupon.subtractUserCount();
            //couponDao.update(coupon);
            order = new Order(cartItems, request.getDelivery_address(),
                    customer, restaurant, LocalDateTime.now(), LocalDateTime.now(), coupon, OrderStatus.unpaid);
        }

        orderDao.save(order);

        return new OrderDto(order);
    }

    public OrderDto getOrder(long orderId) {
        Order order = orderDao.findById(orderId);
        if(order == null) throw new NotFoundException("Order doesn't exist");
        return new OrderDto(order);
    }

    public List<OrderDto> searchOrderHistory(String search, String vendor, Customer customer){
        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String vendorFilter = (vendor == null || vendor.isBlank()) ? "" : vendor.toLowerCase();

        List<Order> allOrders = orderDao.findAll().stream()
                .filter(o -> o.getCustomer().getId().equals(customer.getId())).toList();

        List<String> searchFields = List.of("deliveryAddress", "coupon.code", "status",
                "delivery.fullName", "restaurant.name", "createdAt", "updatedAt");

        Map<String, String> filters = new HashMap<>();
        if (!vendorFilter.isBlank()) filters.put("restaurant.name", vendorFilter);

        List<Order> result =
                SearchUtil.search(allOrders, Order.class, searchFilter, searchFields, filters);

        return result.stream().map(OrderDto::new).toList();
    }

    public void addToFavorites(User user, Restaurant restaurant) {
        User u =  userDao.findByIdLoadFavorites(user.getId());
        u.addToFavorite(restaurant);
        userDao.update(u);
    }

    public void removeFromFavorites(User user, Restaurant restaurant) {
        User u =  userDao.findByIdLoadFavorites(user.getId());
        u.removeFromFavorite(restaurant);
        userDao.update(u);
    }

    public List<RestaurantDto> getFavorites(User user) {
        User u =  userDao.findByIdLoadFavorites(user.getId());
        return u.getFavoriteRestaurants().stream().map(RestaurantDto::new).toList();
    }

    public void submitOrderRating(OrderRatingDto request, User user) {
        Order order = orderDao.findById(request.getOrder_id());
        if (order == null) {
            throw new NotFoundException("Order not found");
        }
        if (order.getRating() != null) {
            throw new AlreadyExistsException("Order already rated");
        }

        //Submit order rating
        OrderRating rating = new OrderRating();
        rating.setComment(request.getComment());
        rating.setImageBase64(request.getImageBase64() == null ? null : request.getImageBase64());
        rating.setRating(request.getRating());
        rating.setOrder(order);
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
            .map(ItemRatingResponseDto::new).toList();

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

    public void updateItemRating(ItemRatingRequestDto request, User user, long ratingId) {
        if(request == null) throw new InvalidInputException("Invalid request");

        ItemRating itemRating = itemRatingDao.findById(ratingId);
        if(itemRating == null) throw new NotFoundException("This rating doesn't exist");

        if(itemRating.getUser().getId() != user.getId())
            throw new UnauthorizedUserException("You can't edit this rating");
        
        if(request.getRating() != null) itemRating.setRating(request.getRating());
        if(request.getComment() != null || !request.getComment().isBlank()) itemRating.setComment(request.getComment());
        if(request.getImageBase64() != null || !request.getImageBase64().isEmpty()) itemRating.setImageBase64(request.getImageBase64());

        itemRatingDao.update(itemRating);
    }


    public List<ItemDto> getRestaurantItems(Restaurant restaurant) {
        return  restaurant.getItems().stream().map(ItemDto::new).toList();
    }

    public void modifyCartItemQuantity(CartItemDto cartItem, Customer customer) {
        Item item = new ItemDao().findById(cartItem.getItem_id());
        Order order = new OrderDao().findAll().stream().filter(
                o -> o.getCustomer().getId() == customer.getId() &&
                o.getStatus().equals(OrderStatus.unpaid) &&
                o.getRestaurant().getId().equals(item.getRestaurant().getId())
                ).findFirst().orElse(null);
        if(order == null && cartItem.getQuantity() > 0) {
            OrderRegistrationRequest orderDto = new OrderRegistrationRequest();
            orderDto.getItems().add(cartItem);
            Restaurant restaurant = item.getRestaurant();
            orderDto.setVendor_id(restaurant.getId());
            Coupon coupon = null;
            addOrder(orderDto, customer, restaurant, coupon);
        }if(order == null && cartItem.getQuantity() < 0) {
            return;
        } else{
            List<CartItem> cartItems = order.getCartItems();
            for (CartItem ci : cartItems) {
                if (ci.getItem().getId() == (item.getId())) {
                    int num = ci.getQuantity() + cartItem.getQuantity();
                    if (num == 0) {
                        cartItems.remove(ci);
                    } else if(num <= item.getSupply()){
                        ci.setQuantity(num);
                    }
                    if(cartItems.isEmpty()){
                        orderDao.delete(order.getId());
                    }else {
                        orderDao.update(order);
                    }
                    return;
                }
            }
            CartItem newCartItem = new CartItem();
            newCartItem.setItem(item);
            newCartItem.setQuantity(cartItem.getQuantity());
            newCartItem.setUser(customer);
            cartItems.add(newCartItem);
            orderDao.update(order);
        }

    }

    public CartItemDto getCartItemQuantity(Customer customer, Item item) {
        //Customer cust = (Customer) customerDao.findByIdLoadCartItems(customer.getId());

        Order order = orderDao.findAll().stream().filter(o ->
                o.getCustomer().getId().equals(customer.getId()) &&
                        o.getStatus().equals(OrderStatus.unpaid) &&
                        o.getRestaurant().getId().equals(item.getRestaurant().getId())
        ).findFirst().orElse(null);

        if(order == null) return new CartItemDto(item.getId(), 0);
        CartItem cartItem = order.getCartItems().stream().filter(o -> o.getItem().getId().equals(item.getId())).findFirst().orElse(null);
        if(cartItem == null) return new CartItemDto(item.getId(), 0);
        else return new CartItemDto(cartItem.getItem().getId(), cartItem.getQuantity());
    }
}