package com.example.Restaurant.Allergy.Review.Application.Controller;

import com.example.Restaurant.Allergy.Review.Application.Model.*;
import com.example.Restaurant.Allergy.Review.Application.Repository.DiningReviewRepository;
import com.example.Restaurant.Allergy.Review.Application.Repository.RestaurantRepository;
import com.example.Restaurant.Allergy.Review.Application.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
public class RestaurantAppController {
    @Autowired
    private final RestaurantRepository restaurantRepository;
    @Autowired
    private final DiningReviewRepository diningReviewRepository;
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public RestaurantAppController(RestaurantRepository restaurantRepository, DiningReviewRepository diningReviewRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.diningReviewRepository = diningReviewRepository;
        this.userRepository = userRepository;
    }



    @GetMapping(value = "/restaurants/search", produces = { "application/json" })
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
        } else if (allergyFocus == AllergyFocus.DAIRY) {
            return this.restaurantRepository.findByZipcodeAndDairyScoreNotNullOrderByDairyScoreDesc(zipcode);
        } else if (allergyFocus == AllergyFocus.ALL) {
            return this.restaurantRepository.findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(zipcode);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/restaurants/search/{id}", produces = { "application/json" })
    public Restaurant searchSpecificRestaurant(@PathVariable("id") Integer id) {
        Optional<Restaurant> specificRestaurantOptional = this.restaurantRepository.findById(id);
        if (specificRestaurantOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return specificRestaurantOptional.get();
        }
    }

    @GetMapping(value = "/user/search/{username}", produces = { "application/json" })
    public User searchSpecificUser(@PathVariable("username") String username) {
        Optional<User> specificUserOptional = this.userRepository.findByUsername(username);
        if (specificUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return specificUserOptional.get();
        }
    }

    @GetMapping(value = "/restaurants/search/{id}/reviews", produces = { "application/json" })
    public List<DiningReview> searchDiningReviews(@PathVariable("id") Integer id) {
        List<DiningReview> diningReviews = this.diningReviewRepository.findByRestaurantIdAndReviewStatus(id, ReviewStatus.ACCEPTED);
        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if (diningReviews.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return diningReviews;
        }
    }

    @GetMapping(value = "/admin/reviews/search", produces = { "application/json" })
    public List<DiningReview> adminSearchDiningReviews(@RequestParam String reviewStatus) {
        List<DiningReview> diningReviews = this.diningReviewRepository.findByReviewStatus(ReviewStatus.valueOf(reviewStatus.toUpperCase()));
        if (diningReviews.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return diningReviews;
        }
    }

    @PutMapping(value = "/user/{username}/settings", produces = { "application/json" })
    public User updateUserSettings(@PathVariable("username") String username, @RequestBody User newUser) {
        Optional<User> existingUserOptional = this.userRepository.findByUsername(username);
        if (existingUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
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

    @PutMapping(value = "/admin/reviews/search/{id}", produces = { "application/json" })
    public DiningReview updateDiningReviewStatus(@PathVariable("id") Integer id, @RequestParam String reviewStatus) {
        Optional<DiningReview> existingDiningReviewOptional = this.diningReviewRepository.findById(id);
        if (existingDiningReviewOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            DiningReview diningReviewToUpdate = existingDiningReviewOptional.get();
            diningReviewToUpdate.setReviewStatus(ReviewStatus.valueOf(reviewStatus.toUpperCase()));
            if (diningReviewToUpdate.getReviewStatus() == ReviewStatus.ACCEPTED) {
                Optional<Restaurant> restaurantToUpdateOptional = this.restaurantRepository.findById(diningReviewToUpdate.getRestaurantId());
                if (restaurantToUpdateOptional.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                }
                Restaurant restaurantToUpdate = restaurantToUpdateOptional.get();
                this.diningReviewRepository.save(diningReviewToUpdate);
                updateRestaurantScores(restaurantToUpdate);
            }
            return this.diningReviewRepository.save(diningReviewToUpdate);
        }
    }


    @PostMapping(value = "/restaurants/submit", produces = { "application/json" })
    public Restaurant submitRestaurant(@RequestBody Restaurant restaurant) {
        Optional<Restaurant> restaurantToTestOptional = this.restaurantRepository.findByZipcodeAndName(restaurant.getZipcode(), restaurant.getName());
        if (restaurantToTestOptional.isEmpty()) {
            return this.restaurantRepository.save(restaurant);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This specific restaurant is already in the database!");
        }
    }

    @PostMapping(value = "/user/creation", produces = { "application/json" })
    public User createNewUser(@RequestBody User user) {
        Optional<User> userToTest = this.userRepository.findByUsername(user.getUsername());
        if (userToTest.isEmpty()) {
            return this.userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This username is already taken!");
        }
    }

    @PostMapping(value = "/restaurants/search/{id}/reviews/creation", produces = { "application/json" })
    public DiningReview createNewReview(@PathVariable("id") Integer id, @RequestParam String username, @RequestBody DiningReview diningReview) {
        Optional<User> userValidationOptional = this.userRepository.findByUsername(username);
        if (userValidationOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        diningReview.setRestaurantId(id);
        diningReview.setCreatorName(username);
        Optional<DiningReview> diningReviewToTest =
                this.diningReviewRepository.findByCreatorNameAndRestaurantId(diningReview.getCreatorName(), diningReview.getRestaurantId());
        if (diningReviewToTest.isEmpty()) {
            diningReview.setReviewStatus(ReviewStatus.PENDING_APPROVAL);
            return this.diningReviewRepository.save(diningReview);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a review for this restaurant under this username!");
        }
    }

    public void updateRestaurantScores(Restaurant restaurantToUpdate) {
        // Compute peanut score for restaurant
        List<DiningReview> diningReviewsWithPeanutScore =
                this.diningReviewRepository.findByRestaurantIdAndReviewStatusAndPeanutScoreNotNull(restaurantToUpdate.getId(), ReviewStatus.ACCEPTED);
        if (!diningReviewsWithPeanutScore.isEmpty()) {
            if (restaurantToUpdate.getPeanutScore() == null) {
                DiningReview firstDiningReview = diningReviewsWithPeanutScore.get(0);
                restaurantToUpdate.setPeanutScore(BigDecimal.valueOf(firstDiningReview.getPeanutScore()));
            } else {
                int amountOfPeanutReviews = diningReviewsWithPeanutScore.size();
                int totalPeanutScore = 0;
                for (DiningReview currentDiningReview : diningReviewsWithPeanutScore) {
                    int currentPeanutScore = currentDiningReview.getPeanutScore();
                    totalPeanutScore += currentPeanutScore;
                }
                BigDecimal peanutScoreBD = new BigDecimal(Integer.toString(totalPeanutScore));
                BigDecimal peanutReviewsBD = new BigDecimal(Integer.toString(amountOfPeanutReviews));
                BigDecimal averagePeanutScore = peanutScoreBD.divide(peanutReviewsBD, 2, RoundingMode.HALF_UP);
                restaurantToUpdate.setPeanutScore(averagePeanutScore);
            }
        }
        // Compute egg score for restaurant
        List<DiningReview> diningReviewsWithEggScore =
                this.diningReviewRepository.findByRestaurantIdAndReviewStatusAndEggScoreNotNull(restaurantToUpdate.getId(), ReviewStatus.ACCEPTED);
        if (!diningReviewsWithEggScore.isEmpty()) {
            if (restaurantToUpdate.getEggScore() == null) {
                DiningReview firstDiningReview = diningReviewsWithEggScore.get(0);
                restaurantToUpdate.setEggScore(BigDecimal.valueOf(firstDiningReview.getEggScore()));
            } else {
                int amountOfEggReviews = diningReviewsWithEggScore.size();
                int totalEggScore = 0;
                for (DiningReview currentDiningReview : diningReviewsWithEggScore) {
                    int currentEggScore = currentDiningReview.getEggScore();
                    totalEggScore += currentEggScore;
                }
                BigDecimal eggScoreBD = new BigDecimal(Integer.toString(totalEggScore));
                BigDecimal eggReviewsBD = new BigDecimal(Integer.toString(amountOfEggReviews));
                BigDecimal averageEggScore = eggScoreBD.divide(eggReviewsBD, 2, RoundingMode.HALF_UP);
                restaurantToUpdate.setEggScore(averageEggScore);
            }
        }
        // Compute dairy score for restaurant
        List<DiningReview> diningReviewsWithDairyScore =
                this.diningReviewRepository.findByRestaurantIdAndReviewStatusAndDairyScoreNotNull(restaurantToUpdate.getId(), ReviewStatus.ACCEPTED);
        if (!diningReviewsWithDairyScore.isEmpty()) {
            if (restaurantToUpdate.getDairyScore() == null) {
                DiningReview firstDiningReview = diningReviewsWithDairyScore.get(0);
                restaurantToUpdate.setDairyScore(BigDecimal.valueOf(firstDiningReview.getDairyScore()));
            } else {
                int amountOfDairyReviews = diningReviewsWithDairyScore.size();
                int totalDairyScore = 0;
                for (DiningReview currentDiningReview : diningReviewsWithDairyScore) {
                    int currentDiaryScore = currentDiningReview.getDairyScore();
                    totalDairyScore += currentDiaryScore;
                }
                BigDecimal dairyScoreBD = new BigDecimal(Integer.toString(totalDairyScore));
                BigDecimal dairyReviewsBD = new BigDecimal(Integer.toString(amountOfDairyReviews));
                BigDecimal averageDiaryScore = dairyScoreBD.divide(dairyReviewsBD, 2 , RoundingMode.HALF_UP);
                restaurantToUpdate.setDairyScore(averageDiaryScore);
            }
        }
        // Compute overall score for restaurant
        BigDecimal totalRestaurantScore = (restaurantToUpdate.getDairyScore().add(restaurantToUpdate.getEggScore())).add(restaurantToUpdate.getPeanutScore());
        BigDecimal divisor = new BigDecimal("3");
        BigDecimal averageRestaurantScore = totalRestaurantScore.divide(divisor, 2, RoundingMode.HALF_UP);
        restaurantToUpdate.setOverallScore(averageRestaurantScore);
        this.restaurantRepository.save(restaurantToUpdate);
    }
}







