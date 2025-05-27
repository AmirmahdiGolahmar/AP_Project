package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @ElementCollection
    @CollectionTable(name = "item_comments", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "comment")
    private List<String> keywords;

    @ElementCollection
    @CollectionTable(name = "item_comments", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "comment")
    private List<String> comments;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToMany
    @JoinTable(
            name = "item_categories",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    private double rating;

    public Item(String name, String photo, String description, double price, int supply, Restaurant restaurant, List<Category> categories, String keywords, double rating) {
        this.name = name;
        this.photo = photo;
        this.description = description; //nullable
        this.price = price;
        this.supply = supply;
        this.restaurant = restaurant;
        this.categories = categories;
        this.keywords = new ArrayList<>();
        this.rating = rating;
    }
}

