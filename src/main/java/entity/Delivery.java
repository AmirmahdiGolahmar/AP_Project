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

    public Delivery(String full_name, String mobile, UserRole role, String email,
                    String photo, String address, Bank_info bank_info, String password) {
        super(full_name, mobile, role, email, photo, address, bank_info, password);
        this.totalDeliveries = 0;
        this.available = false;
        this.averageRating = 0.0;
        this.isApproved = false;
    }

}