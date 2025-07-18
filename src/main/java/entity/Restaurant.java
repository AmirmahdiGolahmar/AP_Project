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

    public Restaurant() {}

    public Restaurant(String name, Seller seller, String address, String phone, String logo,
                        Double taxFee, Double additionalFee) {
        this.name = name;
        this.seller = seller;
        this.address = address;
        this.phone = phone;
        this.logo = logo;
        this.taxFee = taxFee;
        this.additionalFee = additionalFee;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
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

