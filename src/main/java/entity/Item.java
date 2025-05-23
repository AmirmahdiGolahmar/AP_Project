package entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
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

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getSupply() { return supply; }
    public void setSupply(int supply) { this.supply = supply; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }
}
