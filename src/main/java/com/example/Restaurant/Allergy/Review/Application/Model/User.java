package com.example.Restaurant.Allergy.Review.Application.Model;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name="FIRST_NAME")
    private String firstName;
    @Column(name="USERNAME")
    private String username;
    @Column(name="CITY")
    private String city;
    @Column(name="STATE")
    private String state;
    @Column(name="ZIPCODE")
    private String zipcode;
    @Column(name="ALLERGIC_TO_PEANUTS")
    private Boolean allergicToPeanuts;
    @Column(name="ALLERGIC_TO_EGGS")
    private Boolean allergicToEgg;
    @Column(name="ALLERGIC_TO_DIARY")
    private Boolean allergicToDiary;


}
