package entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "deliveries")
public class Delivery extends User {
    @Column(nullable = true)
    private Boolean available;
    //private boolean isApproved;

    @Column(nullable = true)
    private Double latitude;
    @Column(nullable = true)
    private Double longitude;
    @Column(nullable = true)
    private Double averageRating;
    @Column(nullable = true)
    private Integer totalDeliveries;

    public Delivery() {
    }

    public Delivery(String fullName, String mobile, UserRole role, String email,
                    String photo, String address, BankInfo bankInfo, String password) {
        super(fullName, mobile, role, email, photo, address, bankInfo, password);
        this.totalDeliveries = 0;
        this.available = false;
        this.averageRating = 0.0;
        //this.isApproved = false;
    }

    public Delivery(String mobile){
        super(mobile);
        this.totalDeliveries = 0;
        this.available = false;
        this.averageRating = 0.0;
        //this.isApproved = false;
    }

    @Getter
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Order> orders;
    
    public void addOrder(Order order) {
        if (orders == null) {
            orders = new java.util.ArrayList<>();
        }
        orders.add(order);
    }

}