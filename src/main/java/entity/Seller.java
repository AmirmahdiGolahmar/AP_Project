package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller extends User {

    public Seller() {}

    public Seller(String firstName, String lastName,String mobile,
                         String email, String photo, String address, BankInfo bankInfo,String password
                         ,String restaurantDescription) {
        super(firstName, lastName, mobile,UserRole.SELLER, email, photo, address, bankInfo, password);
        this.restaurantDescription = restaurantDescription;
    }
    private String restaurantDescription;

    public String getRestaurantDescription() { return restaurantDescription; }
    public void setRestaurantDescription(String restaurantDescription)
    { this.restaurantDescription = restaurantDescription; }

}