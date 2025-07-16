package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sellers")
public class Seller extends User {

    //private boolean isApproved;

    public Seller() {}
    public Seller(String fullName,String mobile,
                         String email, String photo, String address, BankInfo bankInfo,String password) {
        super(fullName, mobile,UserRole.SELLER, email, photo, address, bankInfo, password);
        //this.isApproved = false;
    }
    public Seller(String mobile){
        super(mobile);
        //this.isApproved = false;
    }

    // public void setIsApproved(boolean isApproved) {
    //     this.isApproved = isApproved;
    // }

    // public boolean getIsApproved() {return this.isApproved;}

}