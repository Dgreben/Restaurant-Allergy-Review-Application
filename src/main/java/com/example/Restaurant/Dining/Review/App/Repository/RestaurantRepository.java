package com.example.Restaurant.Dining.Review.App.Repository;

import com.example.Restaurant.Dining.Review.App.Model.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RestaurantRepository extends CrudRepository<Restaurant, Integer> {
    List<Restaurant> findByZipcode(Integer zipcode);
    List<Restaurant> findByZipcodeAndName(Integer zipcode, String name);
    List<Restaurant> findByZipcodeAndPeanutScoreNotNull(Integer zipcode);
    List<Restaurant> findByZipcodeAndEggScoreNotNull(Integer zipcode);
    List<Restaurant> findByZipcodeAndDairyScoreNotNull(Integer zipcode);
    List<Restaurant> findByZipcodeAndOverallScoreNotNull(Integer zipcode);
}
