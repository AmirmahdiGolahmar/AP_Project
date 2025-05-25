package validator;

import dao.RestaurantDao;
import dao.UserDao;
import entity.Restaurant;
import entity.User;
import entity.UserRole;
import exception.AccessDeniedException;
import exception.NotFoundException;
import exception.SellerNotFoundException;

import java.util.Map;


public class SellerValidator {
    public static void validateSellerAndRestaurant(String userId, Long restaurantId) {

        Restaurant restaurant = new RestaurantDao().findById(restaurantId);

        User seller = new UserDao().findById(Long.parseLong(userId));
        if (seller == null) {
           throw new SellerNotFoundException("there is no seller with this mobile");
        }

        if(seller.getRole() != UserRole.SELLER){
            throw new AccessDeniedException("only seller can edit restaurant info");
        }

        if (restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }

        if (!restaurant.getSeller().getId().equals(seller.getId())) {
            throw new AccessDeniedException("You are not authorized to update this restaurant");
        }
    }
}
