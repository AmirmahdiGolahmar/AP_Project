package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends User {

    public Customer() {}
    public Customer(String fullName, String mobile,UserRole role,String email, String photo,
                    String address, BankInfo bankInfo, String password) {
      super(fullName,mobile,role,email, photo,
        address, bankInfo, password);
    }
}
