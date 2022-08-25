package com.example.Restaurant.Allergy.Review.Application.Model;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name="DINING_REVIEWS")
public class DiningReview {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name="CREATOR_NAME")
    private String creatorName;
    @Column(name="RESTAURANT_ID")
    private Integer restaurantId;
    @Column(name="PEANUT_SCORE")
    private Integer peanutScore;
    @Column(name="EGG_SCORE")
    private Integer eggScore;
    @Column(name="DAIRY_SCORE")
    private Integer dairyScore;
    @Column(name="COMMENTARY")
    private String commentary;
    @Column(name="REVIEW_STATUS")
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;


}
