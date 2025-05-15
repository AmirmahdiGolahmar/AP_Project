package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public User(String firstName, String lastName, String mobile,String email, String photo, String address, BankInfo bankInfo) {
        super(firstName, lastName, mobile,email, photo, address, bankInfo);
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName=" + getFirstName() +
                ", lastName=" + getLastName() +
                ", mobileNumber=" + getMobile() +
                ", email=" + getEmail() +
                ", photo=" + getPhoto() +
                ", address=" + getAddress() +
                ", bankInfo=" + getBankInfo() +
                '}';
    }
}
