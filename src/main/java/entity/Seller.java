package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sellers")
public class Seller extends User {

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Restaurant> restaurants;

    public Seller() {}

    public Seller(String firstName, String lastName,String mobile,
                         String email, String photo, String address, BankInfo bankInfo,String password) {
        super(firstName, lastName, mobile,UserRole.SELLER, email, photo, address, bankInfo, password);
        this.restaurants = new ArrayList<>();
    }
}