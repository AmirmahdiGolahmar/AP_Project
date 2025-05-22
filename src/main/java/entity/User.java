package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    public User() { }

    public User(String firstName,String lastName, String mobile,UserRole role,String email, String photo,
                String address, BankInfo bankInfo, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.role = role;
        this.email = email;
        this.photo = photo;
        this.address = address;
        this.bankInfo = bankInfo;
        this.password = password;
        this.username = firstName + "-" + lastName + "-" + mobile;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded @Column(nullable = false)
    private BankInfo bankInfo;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    @Column(nullable = false)
    private String photo;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String mobile;

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

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

}
