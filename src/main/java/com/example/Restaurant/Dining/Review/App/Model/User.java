package com.example.Restaurant.Dining.Review.App.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="USERNAMES")
    private String username;
    @Column(name="CITIES")
    private String city;
    @Column(name="STATES")
    private String state;
    @Column(name="ZIPCODES")
    private String zipcode;
    @Column(name="ALLERGIC_TO_PEANUTS")
    private Boolean allergicToPeanuts;
    @Column(name="ALLERGIC_TO_EGG")
    private Boolean allergicToEgg;
    @Column(name="ALLERGIC_TO_DIARY")
    private Boolean allergicToDiary;


}
