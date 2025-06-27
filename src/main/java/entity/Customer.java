package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {

    public Customer() {}
    public Customer(String fullName, String mobile,UserRole role,String email, String photo,
                    String address, BankInfo bankInfo, String password) {
      super(fullName,mobile,role,email, photo,
        address, bankInfo, password);
    }
    public Customer(String mobile){
        super(mobile);
    }

//    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
//    private List<Transaction> transactions = new ArrayList<>();
//
//    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
//    private List<Cart> carts = new ArrayList<>();
}
