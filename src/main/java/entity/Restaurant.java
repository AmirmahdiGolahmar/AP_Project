package entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String phone;

    private String logo; // می‌تونه URL باشه یا Base64

    private String workingHours; // مثلاً: "08:00 - 23:00"

    private Double rating;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "restaurant_categories",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    // Getters and Setters ...
}

