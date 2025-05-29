package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String photo;

    private String description;

    private double price;

    private int supply;

    @ManyToMany
    @JoinTable(
            name = "item_categories",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    private String keywords;

    private double rating;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    public Item() {}

    public Item(Long id, String name, String photo, String description, double price,
                int supply, List<Category> categories, String keywords, double rating,
                Restaurant restaurant, List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.price = price;
        this.supply = supply;
        this.categories = categories;
        this.keywords = keywords;
        this.rating = rating;
        this.restaurant = restaurant;
    }

}
