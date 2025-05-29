package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public Customer() {}
    public Customer(String full_name, String mobile, UserRole role, String email, String photo,
                    String address, Bank_info bank_info, String password) {
      super(full_name,mobile,role,email, photo,
        address, bank_info, password);
    }
}

