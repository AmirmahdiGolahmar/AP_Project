package service;
import dao.*;
import dto.RestaurantRegistrationRequest;
import dto.RestaurantResponse;
import dto.RestaurantReturnDto;
import dto.RestaurantUpdateRequest;
import entity.Restaurant;
import entity.Seller;
import entity.User;
import entity.UserRole;
import exception.ForbiddenException;
import spark.Request;
import util.AuthorizationHandler;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantService {
    private final RestaurantDao restaurantDao;
    private final UserService userService = new UserService();

    public RestaurantService() {
        this.restaurantDao = new RestaurantDao();
    }

    public Restaurant createRestaurant(Request request, RestaurantRegistrationRequest restaurantRegistrationRequest) {

        String userId = AuthorizationHandler.authorizeAndExtractUserId(request);
        Seller seller = userService.findSellerById(Long.parseLong(userId));
        if (seller == null || seller.getRole() != UserRole.SELLER) {
            throw new ForbiddenException("Only sellers can create restaurants");
        }
        return createRestaurant(restaurantRegistrationRequest, seller);

    }

    public Restaurant createRestaurant(RestaurantRegistrationRequest restaurantRegistrationRequest, Seller seller) {

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantRegistrationRequest.getName());
        restaurant.setAddress(restaurantRegistrationRequest.getAddress());
        restaurant.setPhone(restaurantRegistrationRequest.getPhone());
        restaurant.setLogo(restaurantRegistrationRequest.getLogoBase64());
        restaurant.setTax_fee(restaurantRegistrationRequest.getTax_fee());
        restaurant.setAdditional_fee(restaurantRegistrationRequest.getAdditional_fee());
        restaurant.setSeller(seller);

        restaurantDao.save(restaurant);

        return restaurant;
    }

    public List<RestaurantReturnDto> findRestaurantsByISellerId(Long id) {
        List<Restaurant> restaurants = restaurantDao.findAllRestaurantsBySellerId(id);
        return restaurants.stream()
                .map(RestaurantReturnDto::new)
                .collect(Collectors.toList());
    }

    public Restaurant findById(Long id) {
        return restaurantDao.findById(id);
    }

    public RestaurantResponse updateRestaurant(Long restaurantId, RestaurantUpdateRequest request) {
        Restaurant restaurant = restaurantDao.findById(restaurantId);
        // اعتبارسنجی و آپدیت
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
            restaurant.setTax_fee(request.getTax_fee());
        }

        if (request.getAdditional_fee() != null) {
            if (request.getAdditional_fee() < 0)
                throw new IllegalArgumentException("Additional fee cannot be negative");
            restaurant.setAdditional_fee(request.getAdditional_fee());
        }

        restaurantDao.update(restaurant);
        return new RestaurantResponse(restaurant);
    }

}
