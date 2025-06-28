package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    private String address;
    private String phone;
    private String logo;
    private String description;
    private String workingHours;
    private int totalOrders;
    private Double averageRating;
    private Double taxFee;
    private Double additionalFee;


    @Getter
    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_item",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )   private List<Item> items;


    public void addItem(Item item) {
        if (items == null) {
            items = new java.util.ArrayList<>();
        }
        items.add(item);
    }
    
    public void removeItem(Item item) {
        if (items != null) {
            items.remove(item);
        }
    }


}

