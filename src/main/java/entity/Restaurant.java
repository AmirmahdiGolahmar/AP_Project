package entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    private String address;
    private String phone;
    private String logo;
    private String image;
    private String description;
    private String workingHours; // مثلاً: "08:00 - 23:00"
    private int totalOrders;
    private double averageRating;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_categories",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Item> items;


}

