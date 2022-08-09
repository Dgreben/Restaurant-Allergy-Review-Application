package com.example.Restaurant.Allergy.Review.Application.Model;

import lombok.Data;


import javax.persistence.*;

@Data
@Entity
@Table(name="RESTAURANTS")
public class Restaurant {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name="NAME")
    private String name;
    @Column(name="ZIPCODE")
    private Integer zipcode;
    @Column(name="CUISINE")
    private String cuisine;
    @Column(name="PEANUT_SCORE")
    private Integer peanutScore;
    @Column(name="EGG_SCORE")
    private Integer eggScore;
    @Column(name="DAIRY_SCORE")
    private Integer dairyScore;
    @Column(name="OVERALL_SCORE")
    private Integer overallScore;

}
