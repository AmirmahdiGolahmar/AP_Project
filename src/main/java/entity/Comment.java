package entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String description;

    @ElementCollection
    @CollectionTable(name = "customer_images", joinColumns = @JoinColumn(name = "customer_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    private int rating;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "confirmed_order_id", nullable = false)
    private Order Order;

    // Constructors
    public Comment() {
        this.createdAt = LocalDateTime.now();
    }

    public List<String> getPhoto() {
        return imageUrls;
    }

}

