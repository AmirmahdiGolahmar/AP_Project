package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller extends User {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private SellerProfile profile;

    public SellerProfile getProfile() { return profile; }
    public void setProfile(SellerProfile profile) { this.profile = profile; }
}