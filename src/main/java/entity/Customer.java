package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends User {

    public Customer() {}
    public Customer(String firstName,String lastName, String mobile,UserRole role,String email, String photo,
                    String address, BankInfo bankInfo, String password) {
      super(firstName,lastName,mobile,role,email, photo,
        address, bankInfo, password);
    }
}
