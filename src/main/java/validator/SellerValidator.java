package validator;

import dao.RestaurantDao;
import dao.UserDao;
import entity.Restaurant;
import entity.User;
import entity.UserRole;
import exception.auth.ForbiddenException;
import exception.common.NotFoundException;


public class SellerValidator {
    public static void validateSellerAndRestaurant(String userId, Long restaurantId) {

        Restaurant restaurant = new RestaurantDao().findById(restaurantId);

        User seller = new UserDao().findById(Long.parseLong(userId));
        if (seller == null) {
           throw new NotFoundException("there is no seller with this mobile");
        }

        if(seller.getRole() != UserRole.SELLER){
            throw new ForbiddenException("only seller can edit restaurant info");
        }

        if (restaurant == null) {
            throw new NotFoundException("Restaurant not found");
        }

        if (!restaurant.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenException("You are not authorized to update this restaurant");
        }
    }
}
