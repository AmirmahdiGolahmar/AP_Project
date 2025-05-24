package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Getter @Setter
    private String address;
    @Getter @Setter
    private String phone;
    @Getter @Setter
    private String logo;
    @Getter @Setter
    private String description;
    private String workingHours;
    @Getter @Setter
    private int totalOrders;
    @Getter @Setter
    private Double averageRating;
    @Getter @Setter
    private Double taxFee;
    @Getter @Setter
    private Double additionalFee;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_categories",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )

    @Getter
    private List<Category> categories;

    @Getter
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Item> items;

    public void addCategory(Category category) {
        categories.add(category);
    }
    public void removeCategory(Category category) {
        categories.remove(category);
    }

    public void addItem(Item item) {
        items.add(item);
    }
    public void removeItem(Item item) {
        items.remove(item);
    }


}

