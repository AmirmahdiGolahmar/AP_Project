package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "deliveries")
public class Delivery extends User {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}
