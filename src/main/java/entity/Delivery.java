package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deliveries")
public class Delivery extends User {

    public Delivery() {}

    public Delivery(String firstName,String lastName, String mobile,UserRole role,String email, String photo,
                    String address, BankInfo bankInfo, String password) {
        super(firstName,lastName,mobile,role,email, photo,
                address, bankInfo, password);
    }
}
