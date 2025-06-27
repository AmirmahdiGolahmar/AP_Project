package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deliveries")
public class Delivery extends User {

    private boolean available;
    private boolean isApproved;

    private double latitude;
    private double longitude;

    private double averageRating;
    private int totalDeliveries;

    public Delivery() {
    }

    public Delivery(String fullName, String mobile, UserRole role, String email,
                    String photo, String address, BankInfo bankInfo, String password) {
        super(fullName, mobile, role, email, photo, address, bankInfo, password);
        this.totalDeliveries = 0;
        this.available = false;
        this.averageRating = 0.0;
        this.isApproved = false;
    }

    public Delivery(String mobile){
        super(mobile);
        this.totalDeliveries = 0;
        this.available = false;
        this.averageRating = 0.0;
        this.isApproved = false;
    }

}