package entity;

import jakarta.persistence.*;

@Entity
public class Seller extends User {
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private SellerProfile profile;

    public SellerProfile getProfile() { return profile; }
    public void setProfile(SellerProfile profile) { this.profile = profile; }
}