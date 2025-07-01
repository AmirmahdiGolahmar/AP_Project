package service;
import dao.*;
import dto.RestaurantRegistrationRequest;
import dto.RestaurantDto;
import dto.RestaurantUpdateRequest;
import entity.Restaurant;
import entity.Seller;
import entity.User;
import exception.AlreadyExistsException;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantService {
    private final RestaurantDao restaurantDao;

    public RestaurantService() {
        this.restaurantDao = new RestaurantDao();
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
}
