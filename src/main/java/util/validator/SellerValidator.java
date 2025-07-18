package util.validator;

import dao.RestaurantDao;
import entity.Restaurant;
import entity.Seller;
import exception.AccessDeniedException;
import exception.NotFoundException;
import exception.SellerNotFoundException;
import exception.UnauthorizedUserException;


public class SellerValidator {
    public static void matchSellerRestaurant(Seller seller, Restaurant restaurant) throws SellerNotFoundException, UnauthorizedUserException, AccessDeniedException {
        if(restaurant.getSeller().getId() != seller.getId()) {
            throw new UnauthorizedUserException("Restaurant and seller do not match");
        }
    }
}
