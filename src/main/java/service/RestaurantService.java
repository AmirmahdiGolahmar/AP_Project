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
import exception.*;
import exception.user.InvalidCredentialsException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import util.AuthorizationHandler;
import spark.*;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantService {
    private final RestaurantDao restaurantDao;
    private final UserService userService = new UserService();

    public RestaurantService() {
        this.restaurantDao = new RestaurantDao();
    }

    public Restaurant createRestaurant(Request request, RestaurantRegistrationRequest restaurantRegistrationRequest) {

        Seller seller = userService.isSeller(Long.parseLong(
                AuthorizationHandler.authorizeAndExtractUserId(request)));
        return createRestaurant(restaurantRegistrationRequest, seller);

    }

    public Restaurant createRestaurant(RestaurantRegistrationRequest restaurantRegistrationRequest, Seller seller) {

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantRegistrationRequest.getName());
        restaurant.setAddress(restaurantRegistrationRequest.getAddress());
        restaurant.setPhone(restaurantRegistrationRequest.getPhone());
        restaurant.setLogo(restaurantRegistrationRequest.getLogoBase64());
        restaurant.setTaxFee(restaurantRegistrationRequest.getTaxFee());
        restaurant.setAdditionalFee(restaurantRegistrationRequest.getAdditionalFee());
        restaurant.setSeller(seller);

        restaurantDao.save(restaurant);

        return restaurant;
    }

    public List<Restaurant> findRestaurantsBySellerId(Request req) {
        String userId = AuthorizationHandler.authorizeAndExtractUserId(req);
        if (userId == null)
            throw new InvalidCredentialsException("No UserId provided");

        Seller seller = userService.isSeller(Long.parseLong(userId));
        return RestaurantDao.findAllRestaurantsBySellerId(Long.parseLong(userId));
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

        if (request.getTaxFee() != null) {
            if (request.getTaxFee() < 0)
                throw new IllegalArgumentException("Tax fee cannot be negative");
            restaurant.setTaxFee(request.getTaxFee());
        }

        if (request.getAdditionalFee() != null) {
            if (request.getAdditionalFee() < 0)
                throw new IllegalArgumentException("Additional fee cannot be negative");
            restaurant.setAdditionalFee(request.getAdditionalFee());
        }

        restaurantDao.update(restaurant);
        return new RestaurantResponse(restaurant);
    }

}
