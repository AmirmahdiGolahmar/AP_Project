package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller extends User {

    private boolean isApproved;

    public Seller() {}
    public Seller(String full_name, String mobile,
                  String email, String photo, String address, Bank_info bank_info, String password) {
        super(full_name, mobile,UserRole.SELLER, email, photo, address, bank_info, password);
        this.isApproved = false;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public boolean getIsApproved() {return this.isApproved;}

}