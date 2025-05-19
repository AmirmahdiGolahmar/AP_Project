package entity;

import jakarta.persistence.Entity;

@Entity
public class SellerProfile extends Profile {

    public SellerProfile() {}

    public SellerProfile(String firstName, String lastName,String mobile,
                         String email, String photo, String address, BankInfo bankInfo,
                         String restaurantDescription) {
        super(firstName, lastName, mobile, email, photo, address, bankInfo);
        this.restaurantDescription = restaurantDescription;
    }
    private String restaurantDescription;

    public String getRestaurantDescription() { return restaurantDescription; }
    public void setRestaurantDescription(String restaurantDescription)
    { this.restaurantDescription = restaurantDescription; }
}
