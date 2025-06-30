package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Item> items;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Menu> menus ;


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

    public void addMenu(Menu menu) {
        if (menus == null) {
            menus = new java.util.ArrayList<>();
        }
        menus.add(menu);
    }

    public void removeMenu(Menu menu) {
        if (menus != null) {
            menus.remove(menu);
        }
    }

}

