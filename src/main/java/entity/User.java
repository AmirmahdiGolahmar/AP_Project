package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    public User(String fullName, String mobile,UserRole role,String email, String photo,
                String address, BankInfo bankInfo, String password) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.photo = photo;
        this.bankInfo = bankInfo;
        this.username = fullName + "-" + mobile;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private UserRole role;

    @Embedded @Column(nullable = false)
    private BankInfo bankInfo;

    @Column(nullable = false)
    private String fullName;

    private String email;

    @Column(nullable = false)
    private String photo;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String mobile;

   public void setBankName(String bankName) {
       this.bankInfo.setBankName(bankName);
   }
   public void setAccountNumber(String accountNumber) {
       this.bankInfo.setAccountNumber(accountNumber);
   }

}
