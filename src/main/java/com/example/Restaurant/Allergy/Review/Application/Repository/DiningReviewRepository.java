package com.example.Restaurant.Allergy.Review.Application.Repository;

import com.example.Restaurant.Allergy.Review.Application.Model.ReviewStatus;
import com.example.Restaurant.Allergy.Review.Application.Model.DiningReview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DiningReviewRepository extends CrudRepository<DiningReview, Integer> {
    List<DiningReview> findByReviewStatus(ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantIdAndReviewStatus(Integer restaurantId, ReviewStatus reviewStatus);
    Optional<DiningReview> findByCreatorNameAndRestaurantId(String creatorName, Integer restaurantId);
}
