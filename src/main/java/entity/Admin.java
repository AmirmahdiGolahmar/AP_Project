package entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Admin extends User {

    @OneToMany
    @JoinColumn(name = "admin_id")
    private List<User> users;

    @OneToMany
    @JoinColumn(name = "admin_id")
    private List<Order> orders;

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

