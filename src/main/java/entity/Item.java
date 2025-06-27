package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String name;

    private String photo;

    private String description;

    private double price;

    private int supply;

    @ElementCollection
    @CollectionTable(name = "item_keywords", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    private double rating;


    @ManyToMany(mappedBy = "items")
    private List<Restaurant> restaurants;

    public Item() {}

    public Item(Long id, String name, String photo, String description, double price,
                int supply, List<String> keywords, double rating) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.price = price;
        this.supply = supply;
        this.keywords = keywords;
        this.rating = rating;
    }

    public void addKeywords(List<String> newKeywords) {
        if (newKeywords == null || newKeywords.isEmpty()) return;

        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }

        this.keywords.addAll(newKeywords);
    }
}
