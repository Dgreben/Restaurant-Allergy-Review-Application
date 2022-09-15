# Restaurant Allergy Review Application

An application API designed to allow users and administrators to create accounts and interact with various restaurants. The focus of the application is for users to judge restaurants based on how well they accomodate for various allergies. Users can submit new restaurants and their respective reviews to the database, which need to be verified by admin-level accounts. Users can also customize their account settings and look up fellow users.

## Built With
[Java] - Programming language used to build the project  
[Spring Boot] - Framework used to create the Spring-based Web Application  
[Lombok] - Library leveraged to reduce boilerplate Java code  
[Maven] - Dependecy Management Library  
[Hibernate] - ORM implemented to develop models and mappings for data  
[H2 Database] - Used to visualize and manipulate data (Test .db file included in project)  
[JUNit5] Testing framework for integration testing  
[Mockito] Testing framework for integration testing  

## Testing
Constructed integration tests on API endpoints using JUnit5 and Mockito. Ensured the endpoints delivered correct data and displayed the proper errors to users in case of an HTTP warning. Tested user data scenarios using Postman.

## Deploying on your system
Fork the Repository and run the main class found in RestaurantAllergyReviewApplication.java  

To view data using the H2 database I leveraged, type the following into your web browser after running the application: http://localhost:9090/h2-console | Username: sa | Password: password  

To test and add data without a database, utilize your web browser or Postman to interact with the endpoints. Ex: (GET) http://localhost:9090/restaurants/search?zipcode=93012&allergyFocus=ALL  

## Built By
Project solely created and designed by Dave Greben
