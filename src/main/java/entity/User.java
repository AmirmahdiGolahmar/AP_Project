package entity;

import exception.AlreadyExistsException;
import exception.NotFoundException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

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

    private String status;

   public void setBankName(String bankName) {
       this.bankInfo.setBankName(bankName);
   }
   public void setAccountNumber(String accountNumber) {
       this.bankInfo.setAccountNumber(accountNumber);
   }

    @ManyToMany
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
            throw new AlreadyExistsException("Restaurant is already favorite");
        }
    }

    public void removeFromFavorite(Restaurant restaurant) {
        if (favoriteRestaurants.stream().anyMatch(r -> r.getId().equals(restaurant.getId()))) {
            this.favoriteRestaurants.removeIf(r -> r.getId().equals(restaurant.getId()));
        }else{
            throw new NotFoundException("This restaurant is not favorite");
        }
    }
}
