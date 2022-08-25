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
    // Endpoint designed to let users search for a list of Restaurants based off of Zipcode and allergyFocus
    @GetMapping(value = "/restaurants/search", produces = { "application/json" })
    public List<Restaurant> searchRestaurants(
            @RequestParam Integer zipcode,
            @RequestParam(required = false) AllergyFocus allergyFocus
    ) {
        // If there is no allergyFocus given, the search with just be based of the Zipcode provided
        if (allergyFocus == null) {
            return this.restaurantRepository.findByZipcode(zipcode);
        } else if (allergyFocus == AllergyFocus.PEANUT) {
            return this.restaurantRepository.findByZipcodeAndPeanutScoreNotNullOrderByPeanutScoreDesc(zipcode);
        } else if (allergyFocus == AllergyFocus.EGG) {
            return this.restaurantRepository.findByZipcodeAndEggScoreNotNullOrderByEggScoreDesc(zipcode);
        } else if (allergyFocus == AllergyFocus.DAIRY) {
            return this.restaurantRepository.findByZipcodeAndDairyScoreNotNullOrderByDairyScoreDesc(zipcode);
            // This focus is designed to search for restaurants based off of their cumulative score across all the categories
        } else if (allergyFocus == AllergyFocus.ALL) {
            return this.restaurantRepository.findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(zipcode);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    // Endpoint designed to let users search for a specific Restaurant ID
    @GetMapping(value = "/restaurants/search/{id}", produces = { "application/json" })
    public Restaurant searchSpecificRestaurant(@PathVariable("id") Integer id) {
        // Validation check for the restaurant ID
        Optional<Restaurant> specificRestaurantOptional = this.restaurantRepository.findById(id);
        if (specificRestaurantOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return specificRestaurantOptional.get();
        }
    }
    // Endpoint designed to let users search for a specific username
    @GetMapping(value = "/user/search/{username}", produces = { "application/json" })
    public User searchSpecificUser(@PathVariable("username") String username) {
        // Validation check for the username String
        Optional<User> specificUserOptional = this.userRepository.findByUsername(username);
        if (specificUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return specificUserOptional.get();
        }
    }
    // Endpoint designed to let users search for dining reviews based off of a Restaurant ID
    @GetMapping(value = "/restaurants/search/{id}/reviews", produces = { "application/json" })
    public List<DiningReview> searchDiningReviews(@PathVariable("id") Integer id) {
        // This endpoint is only designed to only display reviews that are approved
        List<DiningReview> diningReviews = this.diningReviewRepository.findByRestaurantIdAndReviewStatus(id, ReviewStatus.ACCEPTED);
        // Validation check for the restaurant ID
        Optional<Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // Validation check in case the restaurant has zero approved reviews
        else if (diningReviews.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            return diningReviews;
        }
    }
    // Endpoint designed to let admins search for dining reviews based off of their review status
    @GetMapping(value = "/admin/reviews/search", produces = { "application/json" })
    public List<DiningReview> adminSearchDiningReviews(@RequestParam String reviewStatus) {
        List<DiningReview> diningReviews = this.diningReviewRepository.findByReviewStatus(ReviewStatus.valueOf(reviewStatus.toUpperCase()));
        // Validation check in case there are zero reviews of the requested type
        if (diningReviews.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return diningReviews;
        }
    }
    // Endpoint designed to let users update their own settings(except their username)
    @PutMapping(value = "/user/{username}/settings", produces = { "application/json" })
    public User updateUserSettings(@PathVariable("username") String username, @RequestBody User newUser) {
        Optional<User> existingUserOptional = this.userRepository.findByUsername(username);
        // Validation check in case the username in the URI doesn't exist
        if (existingUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            // .setUsername is purposefully omitted as usernames aren't allowed to be altered
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
    /* Endpoint designed to let admins update dining review statuses.
                If the review is approved a validation process will occur for updating Restaurant Scores */
    @PutMapping(value = "/admin/reviews/search/{id}", produces = { "application/json" })
    public DiningReview updateDiningReviewStatus(@PathVariable("id") Integer id, @RequestParam String reviewStatus) {
        Optional<DiningReview> existingDiningReviewOptional = this.diningReviewRepository.findById(id);
        // Validation check for the dining review ID
        if (existingDiningReviewOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            DiningReview diningReviewToUpdate = existingDiningReviewOptional.get();
            diningReviewToUpdate.setReviewStatus(ReviewStatus.valueOf(reviewStatus.toUpperCase()));
            // Validation check for the restaurant ID tied to the dining review
            Optional<Restaurant> restaurantToUpdateOptional = this.restaurantRepository.findById(diningReviewToUpdate.getRestaurantId());
            if (restaurantToUpdateOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
            }
            // If the admin approves the dining review, another process begins to update the Restaurant with the new scores
            if (diningReviewToUpdate.getReviewStatus() == ReviewStatus.ACCEPTED) {
                Restaurant restaurantToUpdate = restaurantToUpdateOptional.get();
                this.diningReviewRepository.save(diningReviewToUpdate);
                updateRestaurantScores(restaurantToUpdate);
            }
            return this.diningReviewRepository.save(diningReviewToUpdate);
        }
    }
    // Endpoint designed to allow users to submit a new restaurant(as long as a restaurant with the same Zipcode and Name don't already exist)
    @PostMapping(value = "/restaurants/submit", produces = { "application/json" })
    public Restaurant submitRestaurant(@RequestBody Restaurant restaurant) {
        Optional<Restaurant> restaurantToTestOptional = this.restaurantRepository.findByZipcodeAndName(restaurant.getZipcode(), restaurant.getName());
        // Duplicate entry check
        if (restaurantToTestOptional.isEmpty()) {
            return this.restaurantRepository.save(restaurant);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This specific restaurant is already in the database!");
        }
    }
    // Endpoint designed to allow users to create a new account(as long as a user with the same username doesn't already exist)
    @PostMapping(value = "/user/creation", produces = { "application/json" })
    public User createNewUser(@RequestBody User user) {
        Optional<User> userToTest = this.userRepository.findByUsername(user.getUsername());
        // Duplicate entry check
        if (userToTest.isEmpty()) {
            return this.userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This username is already taken!");
        }
    }
    // Endpoint designed to allow users to create a new review for a specific Restaurant ID
    @PostMapping(value = "/restaurants/search/{id}/reviews/creation", produces = { "application/json" })
    public DiningReview createNewReview(@PathVariable("id") Integer id, @RequestParam String username, @RequestBody DiningReview diningReview) {
        // Validating that the user exists
        Optional<User> userValidationOptional = this.userRepository.findByUsername(username);
        if (userValidationOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        diningReview.setRestaurantId(id);
        diningReview.setCreatorName(username);
        Optional<DiningReview> diningReviewToTest =
                this.diningReviewRepository.findByCreatorNameAndRestaurantId(diningReview.getCreatorName(), diningReview.getRestaurantId());
        // Duplicate entry check - if there are no duplicates then the review is given a status of pending
        if (diningReviewToTest.isEmpty()) {
            diningReview.setReviewStatus(ReviewStatus.PENDING_APPROVAL);
            return this.diningReviewRepository.save(diningReview);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already a review for this restaurant under this username!");
        }
    }
    // Method used to validate Restaurant scores and update them when a dining review is accepted
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







