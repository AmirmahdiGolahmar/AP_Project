package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import exception.AlreadyExistsException;
import exception.ForbiddenException;
import exception.NotFoundException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import util.PasswordHasher;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class User {

    public User() { }

    public User(String mobile) {
        this.mobile = mobile;
        this.username = mobile;
    }

    public User(String fullName, String mobile,UserRole role,String email, String photo,
                String address, BankInfo bankInfo, String password) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.role = role;
        this.email = email;
        this.photo = photo;
        this.address = address;
        this.bankInfo = bankInfo;
        this.password = password;
        this.username = mobile;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Embedded
    private BankInfo bankInfo = new BankInfo();

    private String fullName;

    private String email;

    private String photo;

    private String address;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Enumerated(EnumType.STRING)
    private UserStatus status =  UserStatus.not_approved;

   public void setBankName(String bankName) {
       this.bankInfo.setBankName(bankName);
   }
   public void setAccountNumber(String accountNumber) {
       this.bankInfo.setAccountNumber(accountNumber);
   }

    @ManyToMany()
    @JoinTable(
            name = "favorite_restaurants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    private List<Restaurant> favoriteRestaurants = new ArrayList<>();

    public void addToFavorite(Restaurant restaurant) {
        if (favoriteRestaurants.stream().noneMatch(r -> r.getId().equals(restaurant.getId()))) {
            this.favoriteRestaurants.add(restaurant);
        }else{
            throw new AlreadyExistsException("Restaurant is already your favorite");
        }
    }

    public void removeFromFavorite(Restaurant restaurant) {
        if (favoriteRestaurants.stream().anyMatch(r -> r.getId().equals(restaurant.getId()))) {
            this.favoriteRestaurants.removeIf(r -> r.getId().equals(restaurant.getId()));
        }else{
            throw new NotFoundException("This restaurant is not you favorite");
        }
    }

    public void deposit(Long amount) {
        this.bankInfo.deposit(amount);
    }
    public void withdraw(Long amount) {
        if(this.bankInfo.getBalance() - amount <= 0) throw new ForbiddenException("Insufficient balance");
        this.bankInfo.withdraw(amount);
    }

    public boolean checkPassword(String password) {
        return PasswordHasher.check(password, this.password);
    }

    public void setPassword(String password) {
        this.password = PasswordHasher.hash(password);
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
}
