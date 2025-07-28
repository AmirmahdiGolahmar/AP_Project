package entity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dto.OrderRatingDto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static util.ImageProcess.base64ToImageFile;
import static util.ImageProcess.imageFileToBase64;

@Getter
@Setter
@Entity
public class OrderRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private int rating;
    private String comment;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_rating_images", joinColumns = @JoinColumn(name = "order_rating_id"))
    @Column(name = "image_base64")
    private List<String> imageBase64;
    LocalDateTime createdAt;


    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    public void setImageBase64(List<String> imageBase64, Long orderId) throws IOException {
        List<String> path = new ArrayList<>();
        int count = 1;
        for(String image : imageBase64) {
           String img = base64ToImageFile(image, "RatingImage" + count + "Order" + orderId);
           count++;
           path.add(img);
        }
        this.imageBase64 = path;
    }

    public List<String> getImageBase64() throws IOException {
        List<String> paths = this.imageBase64;
        List<String> imageBase64 = new ArrayList<>();
        int count = 1;
        for(String path : paths) {
            String img = imageFileToBase64("RatingImage" + count + "Order" + order.getId());
            count++;
            imageBase64.add(img);
        }
        return imageBase64;
    }
}
