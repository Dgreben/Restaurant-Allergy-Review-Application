package com.example.Restaurant.Dining.Review.App.Controller;

import com.example.Restaurant.Dining.Review.App.Model.AllergyFocus;
import com.example.Restaurant.Dining.Review.App.Model.Restaurant;
import com.example.Restaurant.Dining.Review.App.Repository.DiningReviewRepository;
import com.example.Restaurant.Dining.Review.App.Repository.RestaurantRepository;
import com.example.Restaurant.Dining.Review.App.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletOutputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class RestaurantAppController {
    private final RestaurantRepository restaurantRepository;
    private final DiningReviewRepository diningReviewRepository;
    private final UserRepository userRepository;

    public RestaurantAppController(RestaurantRepository restaurantRepository, DiningReviewRepository diningReviewRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/search/restaurants")
    public List<Restaurant> searchRestaurants(
            @RequestParam Integer zipcode,
            @RequestParam(required = false) AllergyFocus allergyFocus
    ) {
        if (allergyFocus == null) {
            return this.restaurantRepository.findByZipcode(zipcode);
        } else if (allergyFocus == AllergyFocus.PEANUT) {
            return this.restaurantRepository.findByZipcodeAndPeanutScoreNotNull(zipcode);
        } else if (allergyFocus == AllergyFocus.EGG) {
            return this.restaurantRepository.findByZipcodeAndEggScoreNotNull(zipcode);
        } else if (allergyFocus == AllergyFocus.DIARY) {
            return this.restaurantRepository.findByZipcodeAndEggScoreNotNull(zipcode);
        } else {
            return null;
        }
    }
    @GetMapping("/search/restaurants/{id}")
    public Restaurant searchSpecificRestaurant(@PathVariable("id") Integer id) {
        Optional<Restaurant> specificRestaurantOptional = this.restaurantRepository.findById(Long.valueOf(id));
        if(!specificRestaurantOptional.isPresent()) {
            return null;
        }
        Restaurant specificRestaurant = specificRestaurantOptional.get();
        return specificRestaurant;
    }
    @PostMapping("/submit/restaurant")
    public Restaurant submitRestaurant(@RequestBody Restaurant restaurant) {
        Integer zipcodeTest = restaurant.getZipcode();
        String nameTest = restaurant.getName();
        if (this.restaurantRepository.findByZipcodeAndName(zipcodeTest, nameTest) == null) {
            return this.restaurantRepository.save(restaurant);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This specific restaurant is already in the database!");
        }
    }
}







