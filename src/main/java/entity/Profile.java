package entity;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "profiles")

public class Profile {
    private String firstName;
    private String lastName;
    private String email;
    private String photo;
    private String address;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Embedded
    private BankInfo bankInfo;
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Profile() {}

    public Profile(String firstName, String lastName,String mobile,String email, String photo, String address, BankInfo bankInfo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.photo = photo;
        this.address = address;
        this.bankInfo = bankInfo;
        this.mobile = mobile;

    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BankInfo getBankInfo() { return bankInfo; }
    public void setBankInfo(BankInfo bankInfo) { this.bankInfo = bankInfo; }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
