package com.example.Restaurant.Allergy.Review.Application.Controller;
import com.example.Restaurant.Allergy.Review.Application.Model.DiningReview;
import com.example.Restaurant.Allergy.Review.Application.Model.Restaurant;
import com.example.Restaurant.Allergy.Review.Application.Model.ReviewStatus;
import com.example.Restaurant.Allergy.Review.Application.Model.User;
import com.example.Restaurant.Allergy.Review.Application.Repository.DiningReviewRepository;
import com.example.Restaurant.Allergy.Review.Application.Repository.RestaurantRepository;
import com.example.Restaurant.Allergy.Review.Application.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
class RestaurantAppControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private RestaurantRepository restaurantRepositoryTest;
    @MockBean
    private UserRepository userRepositoryTest;
    @MockBean
    private DiningReviewRepository diningReviewRepositoryTest;

    Restaurant restaurantTestA = new Restaurant();
    Restaurant restaurantTestB = new Restaurant();
    DiningReview diningReviewTest1A = new DiningReview();
    DiningReview diningReviewTest2A = new DiningReview();
    DiningReview diningReviewTest1B = new DiningReview();
    DiningReview diningReviewTest2B = new DiningReview();
    User userTest1A = new User();
    User userTest2A = new User();


    @BeforeEach()
    // Building context and objects for testing
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();

        restaurantTestA.setId(1);
        restaurantTestB.setId(2);
        diningReviewTest1A.setId(3);
        diningReviewTest2A.setId(4);
        diningReviewTest1B.setId(5);
        diningReviewTest2B.setId(6);
        userTest1A.setId(7);
        userTest2A.setId(8);

        restaurantTestA.setZipcode(93012);
        restaurantTestA.setEggScore(BigDecimal.valueOf(3));
        restaurantTestA.setPeanutScore(BigDecimal.valueOf(3));
        restaurantTestA.setDairyScore(BigDecimal.valueOf(3));
        restaurantTestA.setOverallScore(BigDecimal.valueOf(3));
        restaurantTestA.setName("Drinks");

        restaurantTestB.setZipcode(11111);
        restaurantTestB.setName("Burgers");

        userTest1A.setUsername("Dave");
        userTest1A.setCity("Seattle");
        userTest2A.setCity("York");

        diningReviewTest1A.setRestaurantId(1);
        diningReviewTest1A.setReviewStatus(ReviewStatus.ACCEPTED);
        diningReviewTest1A.setCommentary("Wow!!");

        diningReviewTest2A.setReviewStatus(ReviewStatus.PENDING_APPROVAL);
        diningReviewTest2A.setRestaurantId(2);
    }
        // Testing endpoint "/restaurants/search" with GET mapping
        @Test
        void testSearchRestaurants() throws Exception {
            // Testing endpoint with no allergyFocus parameter
            when(restaurantRepositoryTest.findByZipcode(93012)).thenReturn(List.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search").param("zipcode", "93012"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Drinks"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].zipcode").value(93012)).andReturn().getResponse().getContentAsString();

            // Testing endpoint with allergyFocus parameter of PEANUT
            when(restaurantRepositoryTest.findByZipcodeAndPeanutScoreNotNullOrderByPeanutScoreDesc(93012)).thenReturn(List.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search").param("zipcode", "93012").param("allergyFocus", "PEANUT"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Drinks"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].peanutScore").value(3)).andReturn().getResponse().getContentAsString();

            // Testing endpoint with allergyFocus parameter of EGG
            when(restaurantRepositoryTest.findByZipcodeAndEggScoreNotNullOrderByEggScoreDesc(93012)).thenReturn(List.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search").param("zipcode", "93012").param("allergyFocus", "EGG"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Drinks"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].eggScore").value(3)).andReturn().getResponse().getContentAsString();

            // Testing endpoint with allergyFocus parameter of DAIRY
            when(restaurantRepositoryTest.findByZipcodeAndDairyScoreNotNullOrderByDairyScoreDesc(93012)).thenReturn(List.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search").param("zipcode", "93012").param("allergyFocus", "DAIRY"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Drinks"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].dairyScore").value(3)).andReturn().getResponse().getContentAsString();

            // Testing endpoint with allergyFocus parameter of ALL
            when(restaurantRepositoryTest.findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(93012)).thenReturn(List.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search").param("zipcode", "93012").param("allergyFocus", "ALL"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Drinks"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].overallScore").value(3)).andReturn().getResponse().getContentAsString();

            // Testing endpoint with incorrect allergyFocus parameter
            when(restaurantRepositoryTest.findByZipcodeAndOverallScoreNotNullOrderByOverallScoreDesc(93012)).thenReturn(List.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search").param("zipcode", "93012").param("allergyFocus", "Wrong Parameter"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
        }
        // Testing endpoint "/restaurants/search/{id}" with GET mapping
        @Test
        void testSearchSpecificRestaurant() throws Exception {
            when(restaurantRepositoryTest.findById(1)).thenReturn(Optional.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search/1"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.zipcode").value(93012)).andReturn().getResponse().getContentAsString();

            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search/444"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResponse().getContentAsString();

        }
        // Testing endpoint "/user/search/{username}" with GET mapping
        @Test
        void testSearchSpecificUser() throws Exception {
            when(userRepositoryTest.findByUsername("Dave")).thenReturn(Optional.of(userTest1A));
            mvc.perform(MockMvcRequestBuilders.get("/user/search/Dave"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Seattle")).andReturn().getResponse().getContentAsString();

            mvc.perform(MockMvcRequestBuilders.get("/user/search/WrongParameter"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResponse().getContentAsString();

        }
        // Testing endpoint "/restaurants/search/{id}/reviews" with GET mapping
        @Test
        void testSearchDiningReviews() throws Exception {
            when(diningReviewRepositoryTest.findByRestaurantIdAndReviewStatus(1, ReviewStatus.ACCEPTED)).thenReturn(List.of(diningReviewTest1A));
            mvc.perform(MockMvcRequestBuilders.get("/restaurants/search/1/reviews"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isNotFound())
                    .andReturn().getResponse().getContentAsString();
        }
        // Testing endpoint "/admin/reviews/search" with GET mapping
        @Test
        void testAdminSearchDiningReviews() throws Exception {
            when(diningReviewRepositoryTest.findByReviewStatus(ReviewStatus.PENDING_APPROVAL)).thenReturn(List.of(diningReviewTest2A));
            mvc.perform(MockMvcRequestBuilders.get("/admin/reviews/search").param("reviewStatus", "PENDING_APPROVAL"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        }
        // Testing endpoint "/user/{username}/settings" with PUT mapping
        @Test
        void updateUserSettings() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(userTest1A);

            when(userRepositoryTest.findByUsername("Dave")).thenReturn(Optional.of(userTest1A));
            mvc.perform(MockMvcRequestBuilders.put("/user/Dave/settings").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        }
        // Testing endpoint "/admin/reviews/search/{id}" with PUT mapping
        @Test
        void updateDiningReviewStatus() throws Exception {
            when(diningReviewRepositoryTest.findById(4)).thenReturn(Optional.of(diningReviewTest2A));
            mvc.perform(MockMvcRequestBuilders.put("/admin/reviews/search/4").param("reviewStatus", "REJECTED"))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity()).andReturn().getResponse().getContentAsString();
        }
        // Testing endpoint "/restaurants/submit" with POST mapping
        @Test
        void submitRestaurant() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(restaurantTestA);
            String jsonData2 = mapper.writeValueAsString(restaurantTestB);

            when(restaurantRepositoryTest.findByZipcodeAndName(restaurantTestA.getZipcode(), restaurantTestA.getName())).thenReturn(Optional.of(restaurantTestA));
            mvc.perform(MockMvcRequestBuilders.post("/restaurants/submit").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isConflict()).andReturn().getResponse().getContentAsString();

            when(restaurantRepositoryTest.findByZipcodeAndName(restaurantTestA.getZipcode(), restaurantTestA.getName())).thenReturn(Optional.of(restaurantTestB));
            mvc.perform(MockMvcRequestBuilders.post("/restaurants/submit").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData2))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
        }
        // Testing endpoint "/user/creation" with POST mapping
        @Test
        void createNewUser() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(userTest1A);
            String jsonData2 = mapper.writeValueAsString(userTest2A);

            when(userRepositoryTest.findByUsername(userTest1A.getUsername())).thenReturn(Optional.of(userTest1A));
            mvc.perform(MockMvcRequestBuilders.post("/user/creation").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isConflict()).andReturn().getResponse().getContentAsString();

            when(userRepositoryTest.findByUsername(userTest1A.getUsername())).thenReturn(Optional.of(userTest2A));
            mvc.perform(MockMvcRequestBuilders.post("/user/creation").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData2))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

        }
        // Testing endpoint "/restaurants/search/{id}/reviews/creation" with POST mapping
        @Test
        void createNewReview() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            String jsonData = mapper.writeValueAsString(diningReviewTest1A);

            when(userRepositoryTest.findByUsername(userTest1A.getUsername())).thenReturn(Optional.of(userTest1A));
            mvc.perform(MockMvcRequestBuilders.post("/restaurants/search/1/reviews/creation").param("username", "Dave").contentType(MediaType.APPLICATION_JSON)
                    .content(jsonData))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();

            when(userRepositoryTest.findByUsername(userTest1A.getUsername())).thenReturn(Optional.of(userTest1A));
            mvc.perform(MockMvcRequestBuilders.post("/restaurants/search/1/reviews/creation").param("username", "Wrong Parameter").contentType(MediaType.APPLICATION_JSON)
                            .content(jsonData))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse().getContentAsString();
        }
}


