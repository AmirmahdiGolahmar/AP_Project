package entity;

import exception.ForbiddenException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Coupon")
@Getter
@Setter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(unique = true)
    private String code;

    private CouponType type;
    private Long value;
    private Long minPrice;
    private Long userCount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
     public Coupon() {}

    public Coupon(String code, CouponType type, long value, Long minPrice, Long userCount, LocalDateTime startDate, LocalDateTime endDate) {
         this.code = code;
         this.type = type;
         this.value = value;
         this.minPrice = minPrice;
         this.userCount = userCount;
         this.startDate = startDate;
         this.endDate = endDate;
    }

    public void subtractUserCount(){
         if(this.userCount <= 0) throw new ForbiddenException("Coupon can't be used any more");
         this.userCount--;
    }
}
