package com.example.Restaurant.Allergy.Review.Application.Model;

import lombok.Data;


import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name="RESTAURANTS")
public class Restaurant {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(name="NAME")
    private String name;
    @Column(name="ZIPCODE")
    private Integer zipcode;
    @Column(name="CUISINE")
    private String cuisine;
    @Column(name="PEANUT_SCORE", precision = 3, scale = 2, columnDefinition ="DECIMAL(3,2)")
    private BigDecimal peanutScore;
    @Column(name="EGG_SCORE", precision = 3, scale = 2, columnDefinition="DECIMAL(3,2)")
    private BigDecimal eggScore;
    @Column(name="DAIRY_SCORE", precision = 3, scale = 2, columnDefinition="DECIMAL(3,2)")
    private BigDecimal dairyScore;
    @Column(name="OVERALL_SCORE", precision = 3, scale = 2, columnDefinition="DECIMAL(3,2)")
    private BigDecimal overallScore;

}
