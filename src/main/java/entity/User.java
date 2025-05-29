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

    public User(String full_name, String mobile,UserRole role,String email, String photo,
                String address, Bank_info bank_info, String password) {
        this.full_name = full_name;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.photo = photo;
        this.bank_info = bank_info;
        this.username = full_name + "-" + mobile;
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
    private Bank_info bank_info;

    @Column(nullable = false)
    private String full_name;

    private String email;

    @Column(nullable = false)
    private String photo;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String mobile;

   public void setBank_name(String bank_name) {
       this.bank_info.setBank_name(bank_name);
   }
   public void setAccount_number(String account_number) {
       this.bank_info.setAccount_number(account_number);
   }

}
