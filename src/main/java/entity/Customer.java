package entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {

<<<<<<< HEAD
    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Cart> carts;


=======
    public Customer() {}
    public Customer(String firstName,String lastName, String mobile,UserRole role,String email, String photo,
                    String address, BankInfo bankInfo, String password) {
      super(firstName,lastName,mobile,role,email, photo,
        address, bankInfo, password);
    }
>>>>>>> master
}
