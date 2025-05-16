package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", firstName=" + getFirstName() +
//                ", lastName=" + getLastName() +
//                ", mobileNumber=" + getMobile() +
//                ", email=" + getEmail() +
//                ", photo=" + getPhoto() +
//                ", address=" + getAddress() +
//                ", bankInfo=" + getBankInfo() +
//                '}';
//    }
}
