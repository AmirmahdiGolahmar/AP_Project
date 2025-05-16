package entity;

import jakarta.persistence.*;

@Entity
public class Delivery extends User {
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}
