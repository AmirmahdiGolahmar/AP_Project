package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sellers")
public class Seller extends User {

    public Seller() {}

    public Seller(String firstName, String lastName,String mobile,
                         String email, String photo, String address, BankInfo bankInfo,String password) {
        super(firstName, lastName, mobile,UserRole.SELLER, email, photo, address, bankInfo, password);
    }
}