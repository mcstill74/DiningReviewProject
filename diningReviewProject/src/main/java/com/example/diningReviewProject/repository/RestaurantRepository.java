package com.example.diningReviewProject.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.diningReviewProject.entities.Restaurant;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    Boolean existsByNameAndZipCode(String Name, Integer zip);
    List<Restaurant> getByZipCodeAndOverallScoreIsGreaterThanOrderByNameDesc(Integer zipCode, Integer min ); 
}
