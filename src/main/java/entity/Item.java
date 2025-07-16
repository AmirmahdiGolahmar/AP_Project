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

    private Integer supply;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_keywords", joinColumns = @JoinColumn(name = "item_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;


    public Item() {}

    public Item(Long id, String name, String photo, String description, Double price,
                Integer supply, List<String> keywords, Double rating) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.price = price;
        this.supply = supply;
        this.keywords = keywords;
        this.rating = rating;
    }

    public void subtractSupplyCount(int count){
        this.supply = this.supply - count;
    }

}
