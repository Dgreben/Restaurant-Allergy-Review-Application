package com.example.Restaurant.Dining.Review.App.Repository;

import com.example.Restaurant.Dining.Review.App.Model.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    List<Restaurant> findByZipcode(Integer zipcode);
    Optional<Restaurant> findByZipcodeAndName(Integer zipcode, String name);
    List<Restaurant> findByZipcodeAndPeanutScoreNotNullOrderByPeanutScoreDesc(Integer zipcode);
    List<Restaurant> findByZipcodeAndEggScoreNotNullOrderByEggScoreDesc(Integer zipcode);
    List<Restaurant> findByZipcodeAndDairyScoreNotNullOrderByDairyScoreDesc(Integer zipcode);
    List<Restaurant> findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(Integer zipcode);
}
