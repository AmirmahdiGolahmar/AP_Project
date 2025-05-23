package dao;

import entity.Restaurant;

public class RestaurantDao extends GenericDao<Restaurant> {
    public RestaurantDao() {
        super(Restaurant.class);
    }
}
