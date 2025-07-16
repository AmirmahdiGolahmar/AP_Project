package service;
import dao.*;
import dto.RestaurantRegistrationRequest;
import dto.OrderDto;
import dto.RestaurantDto;
import dto.RestaurantUpdateRequest;
import dto.SearchRestaurantOrdesrDto;
import entity.Order;
import entity.OrderStatus;
import entity.Restaurant;
import entity.Seller;
import entity.User;
import exception.AlreadyExistsException;
import exception.NotFoundException;
import exception.UnauthorizedUserException;
import util.SearchUtil;
import validator.SellerValidator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantService {
    private final RestaurantDao restaurantDao;
    private final OrderDao orderDao;

    public RestaurantService() {
        this.restaurantDao = new RestaurantDao();
        this.orderDao = new OrderDao();
    }

    public Restaurant createRestaurant(RestaurantRegistrationRequest request, User seller) {

        if(restaurantDao.findAll().stream().anyMatch(restaurant ->
                        restaurant.getName().equals(request.getName()) &&
                        restaurant.getAddress().equals(request.getAddress())))
            throw new AlreadyExistsException("This restaurant already exists");


        Restaurant restaurant = new Restaurant(
                request.getName(), (Seller) seller, request.getAddress(),
                request.getPhone(), request.getLogoBase64(),
                request.getTax_fee(), request.getAdditional_fee()
        );

        restaurantDao.save(restaurant);
        return restaurant;
    }

    public List<RestaurantDto> findRestaurantsByISellerId(Long id) {
        List<Restaurant> restaurants = restaurantDao.findAllRestaurantsBySellerId(id);
        return restaurants.stream()
                .map(RestaurantDto::new)
                .collect(Collectors.toList());
    }

    public Restaurant findById(Long id) {
        return restaurantDao.findById(id);
    }

    public RestaurantDto updateRestaurant(Long restaurantId, RestaurantUpdateRequest request) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);
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
            if (request.getTax_fee() < 0)
                throw new IllegalArgumentException("Tax fee cannot be negative");
            restaurant.setTaxFee(request.getTax_fee());
        }

        if (request.getAdditional_fee() != null) {
            if (request.getAdditional_fee() < 0)
                throw new IllegalArgumentException("Additional fee cannot be negative");
            restaurant.setAdditionalFee(request.getAdditional_fee());
        }

        restaurantDao.update(restaurant);
        return new RestaurantDto(restaurant);
    }

    public List<OrderDto> searchRestaurantOrders(
            String search,String courier,String user,String status, Long restaurantId) {

        String searchFilter = (search == null || search.isBlank()) ? "" : search.toLowerCase();
        String courierFilter = (courier == null || courier.isBlank()) ? "" : courier.toLowerCase();
        String userFilter = (user == null || user.isBlank()) ? "" : user.toLowerCase();
        String statusFilter = (status == null || status.isBlank()) ? "" : status.toLowerCase();

        List<Order> allOrders = orderDao.findAll();

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

    public void changeOrderStatus(String userId, Long orderId, String newStatus) {
        Order order = orderDao.findById(orderId);
        if(order == null) throw new NotFoundException("This order doesn't exist");

        SellerValidator.validateSellerAndRestaurant(userId, order.getRestaurant().getId());

        OrderStatus status = OrderStatus.strToStatus(newStatus);

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderDao.update(order);
    }

}
