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
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public void subtractSupplyCount(int count){
        this.supply = this.supply - count;
    }


    public Item() {}

    public Item(String name, String photo, String description, Double price, Integer supply, List<String> keywords, Restaurant restaurant, Double rating) {
        if(name != null) this.name = name;
        if(photo != null) this.photo = photo;
        if(description != null) this.description = description;
        if(price != null) this.price = price;
        if(supply != null) this.supply = supply;
        if(keywords != null) this.keywords = keywords;
        if(restaurant != null) this.restaurant = restaurant;
        if(rating != null) this.rating = rating;
    }

}
