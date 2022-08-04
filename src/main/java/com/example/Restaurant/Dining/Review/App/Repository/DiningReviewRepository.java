package com.example.Restaurant.Dining.Review.App.Repository;

import com.example.Restaurant.Dining.Review.App.Model.DiningReview;
import com.example.Restaurant.Dining.Review.App.Model.ReviewStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DiningReviewRepository extends CrudRepository<DiningReview, Integer> {
    List<DiningReview> findByReviewStatus(ReviewStatus reviewStatus);
    List<DiningReview> findByRestaurantIdAndReviewStatus(Integer restaurantId, ReviewStatus reviewStatus);
}
