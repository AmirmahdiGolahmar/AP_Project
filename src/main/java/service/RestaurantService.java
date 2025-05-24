package service;
import dao.*;
import dto.RestaurantRegistrationRequest;
import dto.RestaurantReturnDto;
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

    public List<RestaurantReturnDto> findAllRestaurants() {
        List<Restaurant> restaurants = restaurantDao.findAll();
        return restaurants.stream()
                .map(RestaurantReturnDto::new)
                .collect(Collectors.toList());
    }


}
