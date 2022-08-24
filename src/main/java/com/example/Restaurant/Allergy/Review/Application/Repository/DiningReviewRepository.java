package com.example.Restaurant.Allergy.Review.Application.Repository;

import com.example.Restaurant.Allergy.Review.Application.Model.ReviewStatus;
import com.example.Restaurant.Allergy.Review.Application.Model.DiningReview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiningReviewRepository extends CrudRepository<DiningReview, Integer> {
    List<DiningReview> findByReviewStatus(ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantIdAndReviewStatus(Integer restaurantId, ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantIdAndReviewStatusAndEggScoreNotNull(Integer restaurantId, ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantIdAndReviewStatusAndPeanutScoreNotNull(Integer restaurantId, ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantIdAndReviewStatusAndDairyScoreNotNull(Integer restaurantId, ReviewStatus reviewStatus);
    Optional<DiningReview> findByCreatorNameAndRestaurantId(String creatorName, Integer restaurantId);
}
