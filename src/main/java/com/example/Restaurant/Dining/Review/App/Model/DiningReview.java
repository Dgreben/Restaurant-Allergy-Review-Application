package com.example.Restaurant.Dining.Review.App.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="DINING_REVIEWS")
public class DiningReview {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="CREATOR_NAME")
    private String creatorName;
    @Column(name="RESTAURANT_ID")
    private Long restaurantId;
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
