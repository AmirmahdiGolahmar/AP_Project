package service;
import dao.*;
import dto.RestaurantRegistrationRequest;
import dto.RestaurantResponse;
import dto.RestaurantReturnDto;
import dto.RestaurantUpdateRequest;
import entity.Restaurant;
import entity.Seller;
import entity.User;
import exception.AlreadyExistsException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.util.List;
import java.util.stream.Collectors;

public class RestaurantService {
    private final RestaurantDao restaurantDao;

    public RestaurantService() {
        this.restaurantDao = new RestaurantDao();
    }

    public void createRestaurant(RestaurantRegistrationRequest request, User seller) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setLogo(request.getLogoBase64());
        restaurant.setTaxFee(request.getTax_fee());
        restaurant.setAdditionalFee(request.getAdditional_fee());
        restaurant.setSeller((Seller) seller);

        restaurantDao.save(restaurant);
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
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }

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
        }

        if (request.getAdditional_fee() != null) {
            if (request.getAdditional_fee() < 0)
                throw new IllegalArgumentException("Additional fee cannot be negative");
            restaurant.setAdditionalFee(request.getAdditional_fee().doubleValue());
        }

        restaurantDao.update(restaurant);

        return new RestaurantResponse(restaurant);
    }


}
