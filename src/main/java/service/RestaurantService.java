package service;
import dao.*;
import dto.*;
import entity.*;
import exception.AlreadyExistsException;
import exception.NotFoundException;
import util.SearchUtil;
import util.validator.SellerValidator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static util.validator.validator.additionalFeeValidator;
import static util.validator.validator.taxFeeValidator;

public class RestaurantService {
    private final RestaurantDao restaurantDao;
    private final OrderDao orderDao;
    private final UserDao userDao;

    public RestaurantService() {
        this.restaurantDao = new RestaurantDao();
        this.orderDao = new OrderDao();
        this.userDao = new UserDao();
    }

    public Restaurant createRestaurant(RestaurantRegistrationRequest request, Seller seller) {
        
        if(restaurantDao.findAll().stream().anyMatch(restaurant ->
                        restaurant.getName().equals(request.getName()) &&
                        restaurant.getAddress().equals(request.getAddress())))
            throw new AlreadyExistsException("This restaurant already exists");


        Restaurant restaurant = new Restaurant(
                request.getName(), seller, request.getAddress(),
                request.getPhone(), request.getLogoBase64(),
                request.getTax_fee(), request.getAdditional_fee()
        );

        restaurantDao.save(restaurant);
        return restaurant;
    }

    public List<RestaurantDto> getSellerRestaurants(Seller seller) {
        List<Restaurant> restaurants = restaurantDao.findAllRestaurantsBySellerId(seller.getId());
        return restaurants.stream()
                .map(RestaurantDto::new)
                .collect(Collectors.toList());
    }

    public Restaurant findById(Long id) {
        return restaurantDao.findById(id);
    }

    public RestaurantDto updateRestaurant(Restaurant restaurant, RestaurantUpdateRequest request) {
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }

        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }

        if (request.getPhone() != null) {
            restaurant.setPhone(request.getPhone());
        }

        if (request.getLogoBase64() != null) {
            restaurant.setLogo(request.getLogoBase64());
        }

        if (request.getTax_fee() != null) {
            taxFeeValidator(request.getTax_fee());
            restaurant.setTaxFee(request.getTax_fee());
        }

        if (request.getAdditional_fee() != null) {
            additionalFeeValidator(request.getAdditional_fee());
            restaurant.setAdditionalFee(request.getAdditional_fee());
        }

        restaurantDao.update(restaurant);
        return new RestaurantDto(restaurant);
    }

    public List<OrderDto> searchRestaurantOrders(
            String search,String courier,String user,String status, Restaurant restaurant) {

        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String courierFilter = (courier == null || courier.isBlank()) ? "" : courier.toLowerCase();
        String userFilter = (user == null || user.isBlank()) ? "" : user.toLowerCase();
        String statusFilter = (status == null || status.isBlank()) ? "" : status.toLowerCase();

        List<Order> allOrders = orderDao.findAll().stream().
                filter(order -> order.getRestaurant().getId().equals(restaurant.getId())).toList();

        List<String> searchFields = List.of("deliveryAddress", "coupon.code", "status",
                "delivery.fullName", "restaurant.name", "customer.fullName", "createdAt", "updatedAt");

        Map<String, String> filters = new HashMap<>();
        if (!courierFilter.isBlank()) filters.put("delivery.fullName", courierFilter);
        if (!userFilter.isBlank()) filters.put("customer.fullName", userFilter);
        if (!statusFilter.isBlank()) filters.put("status", statusFilter);


        List<Order> result =
                SearchUtil.search(allOrders, Order.class, searchFilter, searchFields, filters);

        return result.stream().map(OrderDto::new).toList();
    }

    public void changeOrderStatus(Order order, StatusDto request) {
        OrderStatus status = OrderStatus.strToStatus(request.getStatus());

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderDao.update(order);
    }

}
