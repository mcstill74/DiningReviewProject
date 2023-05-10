package com.example.diningReviewProject.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.diningReviewProject.entities.Review;
import com.example.diningReviewProject.entities.ReviewStatus;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long>{
    List<Review> getAllByStatus(ReviewStatus status);
    List<Review> getAllByRestaurantId(Long id);
    List<Review> getAllByRestaurantIdAndStatus(Long restaurantId, ReviewStatus status);
    
}
