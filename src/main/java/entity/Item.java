package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private String photo;
    @Setter
    @Getter
    private String description;
    @Setter
    @Getter
    private double price;
    @Setter
    @Getter
    private int capacity;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Getter
    @ManyToMany
    @JoinTable(
            name = "item_categories",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    private String keywords;

    private double rating;

    private List<String> comments;

    public Item() {}

    public Item(String name, String photo, String description, double price, int capacity, Restaurant restaurant, List<Category> categories, String keywords, double rating) {
        this.name = name;
        this.photo = photo;
        this.description = description; //nullable
        this.price = price;
        this.capacity = capacity;
        this.restaurant = restaurant;
        this.categories = categories;
        this.keywords = keywords;
        this.rating = rating;
    }
}

