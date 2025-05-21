package entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Admin extends User {
<<<<<<< HEAD

    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany
    @JoinColumn(name = "admin_id") // در جدول User یک ستون admin_id ایجاد می‌شود
    private List<User> users;

    @OneToMany
    @JoinColumn(name = "admin_id") // در جدول Order یک ستون admin_id ایجاد می‌شود
    private List<Order> orders;

    // Getters and Setters

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
=======

}
>>>>>>> master
