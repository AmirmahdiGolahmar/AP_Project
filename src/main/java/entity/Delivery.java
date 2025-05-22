package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deliveries")
public class Delivery extends User {

    @OneToOne(mappedBy = "delivery", cascade = CascadeType.ALL)
    private Order order;

    private boolean available;

    private double latitude;
    private double longitude;

    private double averageRating;
    private int totalDeliveries;

    public Delivery() {}

    public Delivery(String firstName, String lastName, String mobile, UserRole role, String email,
                    String photo, String address, BankInfo bankInfo, String password) {
        super(firstName, lastName, mobile, role, email, photo, address, bankInfo, password);
        this.totalDeliveries = 0;
        this.available = false;
        this.averageRating = 0.0;
    }
}
