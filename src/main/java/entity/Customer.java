package entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Cart> carts;


}
