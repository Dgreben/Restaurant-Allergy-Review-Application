package com.example.Restaurant.Dining.Review.App.Controller;

import com.example.Restaurant.Dining.Review.App.Model.AllergyFocus;
import com.example.Restaurant.Dining.Review.App.Model.Restaurant;
import com.example.Restaurant.Dining.Review.App.Model.User;
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

    @GetMapping("/restaurants/search")
    public List<Restaurant> searchRestaurants(
            @RequestParam Integer zipcode,
            @RequestParam(required = false) AllergyFocus allergyFocus
    ) {
        if (allergyFocus == null) {
            return this.restaurantRepository.findByZipcode(zipcode);
        } else if (allergyFocus == AllergyFocus.PEANUT) {
            return this.restaurantRepository.findByZipcodeAndPeanutScoreNotNullOrderByPeanutScoreDesc(zipcode);
        } else if (allergyFocus == AllergyFocus.EGG) {
            return this.restaurantRepository.findByZipcodeAndEggScoreNotNullOrderByEggScoreDesc(zipcode);
        } else if (allergyFocus == AllergyFocus.DIARY) {
            return this.restaurantRepository.findByZipcodeAndDairyScoreNotNullOrderByDairyScoreDesc(zipcode);
        } else if (allergyFocus == AllergyFocus.ALL) {
            return this.restaurantRepository.findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(zipcode);
        } else {
            return null;
        }
    }

    @GetMapping("/restaurants/search/{id}")
    public Restaurant searchSpecificRestaurant(@PathVariable("id") Integer id) {
        Optional<Restaurant> specificRestaurantOptional = this.restaurantRepository.findById(Long.valueOf(id));
        if(specificRestaurantOptional.isEmpty()) {
            return null;
        }
        else {
           return specificRestaurantOptional.get();
        }
    }
    @GetMapping("/user/search/{username}")
    public User searchSpecificUser(@PathVariable("username") String username) {
        Optional<User> specificUserOptional = this.userRepository.findByUsername(username);
        if(specificUserOptional.isEmpty()) {
            return null;
        }
        else {
            return specificUserOptional.get();
        }
    }
    @PutMapping("user/{username}/settings")
    public User updateUserSettings(@PathVariable("username") String username, @RequestBody User newUser) {
        Optional<User> existingUserOptional = this.userRepository.findByUsername(username);
        if(existingUserOptional.isEmpty()) {
            return null;
        }
        else {
            User existingUser = existingUserOptional.get();
            existingUser.setFirstName(newUser.getFirstName());
            existingUser.setCity(newUser.getCity());
            existingUser.setState(newUser.getState());
            existingUser.setZipcode(newUser.getZipcode());
            existingUser.setAllergicToPeanuts(newUser.getAllergicToPeanuts());
            existingUser.setAllergicToEgg(newUser.getAllergicToEgg());
            existingUser.setAllergicToDiary(newUser.getAllergicToDiary());
            return this.userRepository.save(existingUser);
        }
    }


    @PostMapping("/restaurants/submit")
    public Restaurant submitRestaurant(@RequestBody Restaurant restaurant) {
        Optional<Restaurant> restaurantToTest = this.restaurantRepository.findByZipcodeAndName(restaurant.getZipcode(), restaurant.getName());
        if(restaurantToTest.isEmpty()) {
            return this.restaurantRepository.save(restaurant);
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This specific restaurant is already in the database!");
        }
    }
    @PostMapping("/user/creation")
    public User createNewUser(@RequestBody User user) {
        Optional<User> userToTest = this.userRepository.findByUsername(user.getUsername());
        if(userToTest.isEmpty()) {
            return this.userRepository.save(user);
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This username is already taken!");
        }
    }
}







