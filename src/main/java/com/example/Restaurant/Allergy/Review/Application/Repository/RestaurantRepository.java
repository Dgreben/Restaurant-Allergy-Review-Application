package com.example.Restaurant.Allergy.Review.Application.Repository;

import com.example.Restaurant.Allergy.Review.Application.Model.Restaurant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends CrudRepository<Restaurant, Integer> {
    List<Restaurant> findByZipcode(Integer zipcode);
    Optional<Restaurant> findByZipcodeAndName(Integer zipcode, String name);
    List<Restaurant> findByZipcodeAndPeanutScoreNotNullOrderByPeanutScoreDesc(Integer zipcode);
    List<Restaurant> findByZipcodeAndEggScoreNotNullOrderByEggScoreDesc(Integer zipcode);
    List<Restaurant> findByZipcodeAndDairyScoreNotNullOrderByDairyScoreDesc(Integer zipcode);
    List<Restaurant> findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(Integer zipcode);
}
