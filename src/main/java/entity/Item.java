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

    private String photo; // URL یا Base64

    private String description;

    private double price;

    private int inventory;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String keywords;

    private double rating;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Item() {}

    public Item(Long id, String name, String photo, String description, double price,
                int inventory, String category, String keywords, double rating,
                Restaurant restaurant, List<Comment> comments) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.price = price;
        this.inventory = inventory;
        //Complete the category handling
        this.keywords = keywords;
        this.rating = rating;
        this.restaurant = restaurant;
        this.comments = comments;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getInventory() { return inventory; }
    public void setInventory(int inventory) { this.inventory = inventory; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}

