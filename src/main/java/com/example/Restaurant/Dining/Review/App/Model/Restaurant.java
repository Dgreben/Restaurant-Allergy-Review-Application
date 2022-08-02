package com.example.Restaurant.Dining.Review.App.Model;

import lombok.Data;


import javax.persistence.*;

@Data
@Entity
@Table(name="RESTAURANTS")
public class Restaurant {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="NAMES")
    private String name;
    @Column(name="LOCATIONS")
    private String location;
    @Column(name="CUISINES")
    private String cuisine;
    @Column(name="PEANUT_SCORE")
    private Integer peanutScore;
    @Column(name="EGG_SCORE")
    private Integer eggScore;
    @Column(name="DAIRY_SCORE")
    private Integer dairyScore;
}
