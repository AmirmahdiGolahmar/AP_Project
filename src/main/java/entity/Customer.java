package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Cart> carts;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_info_id", referencedColumnName = "id")
    private BankInfo bankInfo;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Discount> discounts = new ArrayList<>();

    public Customer() {}
    public Customer(String firstName,String lastName, String mobile,UserRole role,String email, String photo,
                    String address, BankInfo bankInfo, String password) {
      super(firstName,lastName,mobile,role,email, photo,
        address, bankInfo, password);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public BankInfo getBankInfo() {
        return bankInfo;
    }

    public void setBankInfo(BankInfo bankInfo) {
        this.bankInfo = bankInfo;
    }
}
