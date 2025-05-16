package entity;

import jakarta.persistence.Entity;

@Entity
public class SellerProfile extends Profile {
    private String restaurantDescription;

    public String getRestaurantDescription() { return restaurantDescription; }
    public void setRestaurantDescription(String restaurantDescription)
    { this.restaurantDescription = restaurantDescription; }
}
