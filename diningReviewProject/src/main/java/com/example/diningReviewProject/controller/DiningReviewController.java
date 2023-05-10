package com.example.diningReviewProject.controller;

import com.example.diningReviewProject.entities.*;
import com.example.diningReviewProject.repository.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


import java.util.Optional;
import java.util.List;

 
@RestController
public class DiningReviewController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
        
    public DiningReviewController(final UserRepository userRepository, final RestaurantRepository restaurantRepository, final ReviewRepository reviewRepository ){
      this.userRepository = userRepository;
      this.restaurantRepository = restaurantRepository;
      this.reviewRepository = reviewRepository;
    } 

    /* Users section requests */
    @GetMapping(value="/users")
    public Iterable<Reviewer> getAllUsers(){
      return this.userRepository.findAll();
    }

    @GetMapping(value="/users/username", produces={"application/json"})
    @ResponseBody
    public Reviewer retrieveUserByUserName(@RequestParam String username){
      Optional<Reviewer> retrieveUserOpt = this.userRepository.findByUserName(username);
      if(!retrieveUserOpt.isPresent()){
        
        throw new ReviewerNotFoundException();
      }
      return retrieveUserOpt.get();
    }

    @PostMapping(value="/addusers", consumes = {"application/json"}, produces={"application/json"})
    @ResponseBody
    public Reviewer createNewUser(@RequestBody Reviewer user){
      boolean userExists =  this.userRepository.existsByUserName(user.getUserName()); 
      if(!userExists){
          return this.userRepository.save(user);
      }
      return user;

    }

    @PutMapping(value="/updateusers", consumes = "application/json", produces="application/json")
    @ResponseBody
      public Reviewer updateAnExistingUser(@RequestParam String username, @RequestBody Reviewer user){
        Optional<Reviewer> updateOptional = this.userRepository.findByUserName(username); 
        if(!updateOptional.isPresent()){
          throw new ReviewerNotFoundException();
        }
        Reviewer userToUpdate = updateOptional.get();

        if( null != user.getName() )
          userToUpdate.setName(user.getName());
        if(null != user.getCity() )
          userToUpdate.setCity(user.getCity());
        if(null != user.getState() )
          userToUpdate.setState(user.getState());
        if(null != user.getZipCode() )
          userToUpdate.setZipCode(user.getZipCode());
        if(null != user.getDairyAllergy() && !user.getDairyAllergy().equals(userToUpdate.getDairyAllergy())  )
          userToUpdate.setDairyAllergy(user.getDairyAllergy());
        if(null != user.getPeanutAllergy() && !user.getPeanutAllergy().equals(userToUpdate.getPeanutAllergy()))
          userToUpdate.setPeanutAllergy(user.getPeanutAllergy());
        if(null != user.getEggAllergy() && !user.getEggAllergy().equals(userToUpdate.getEggAllergy()))
          userToUpdate.setEggAllergy(user.getEggAllergy());
        
        return this.userRepository.save(userToUpdate);  
    }
    /* end of users section */

    /* Reviews section requests */
    @PostMapping(value="/addreview", consumes = "application/json", produces="application/json")
    @ResponseBody
    public Review createNewReview(@RequestBody Review review) {
        String strName = review.getSubmitter();
        if(!this.userRepository.existsByUserName(strName))
          throw new ReviewerNotFoundException();
        else if(!this.restaurantRepository.existsById(review.getRestaurantId()))
          throw new RestaurantNotFoundException();
        AdminReview admin = new AdminReview(review);
        return this.reviewRepository.save(admin.getReviewObject() );
    }

    @GetMapping(value="/admin/adminReviewList")
    public List<Review> getPendingReviews(){
      return this.reviewRepository.getAllByStatus(ReviewStatus.PENDING);
    }

    @PutMapping(value="/admin/updatereview/{id}", produces={"application/json"})
    @ResponseBody
    public List<Review> adminUpdateReviewStatus(@PathVariable("id") Long id, @RequestParam String status){
      Optional<Review> pendingOptional = this.reviewRepository.findById(id);
      if(!pendingOptional.isPresent() ){
        throw new ReviewNotFoundException();
      }
      Review reviewUpdate = pendingOptional.get();

      Optional<Restaurant> restOptional = this.restaurantRepository.findById(reviewUpdate.getRestaurantId());
      if(!restOptional.isPresent() ){
        throw new RestaurantNotFoundException(); 
      }
      Restaurant updateRestScores = restOptional.get();

      /*upon the accept status, need to submit update to restaurant to update the lists of scores and overall score */
      if(status.equals("ACCEPT") ){
        updateRestaurantScores(updateRestScores, reviewUpdate);
        reviewUpdate.setStatus(ReviewStatus.ACCEPT);
      }
      else if(status.equals("REJECT")){
        reviewUpdate.setStatus(ReviewStatus.REJECT);
      }
      this.reviewRepository.save(reviewUpdate);
      
      return this.reviewRepository.getAllByRestaurantIdAndStatus(id, ReviewStatus.ACCEPT);
    }

    private void updateRestaurantScores(Restaurant updateScores, Review review){
      if(review.getDairyScore() > 0){
        updateScores.setDairy(review.getDairyScore());
      }
        
      if(review.getEggScore() > 0){
        updateScores.setEgg(review.getEggScore());
      }

      if(review.getPeanutScore() > 0){
        updateScores.setPeanut(review.getPeanutScore());
      }
      updateScores.setOverallScore(calculateOverallScore(updateScores));
      this.restaurantRepository.save(updateScores);

    }

    /* Separate method to calculate out overallScore and set */
    private Integer calculateOverallScore(Restaurant tallyRestaurant){

      return (tallyRestaurant.getDairy() + tallyRestaurant.getEgg() + tallyRestaurant.getPeanut())/3;
    }

      /* restaurant section requests */
    @GetMapping(value="/restaurantApprovedReviews/{restaurantId}", produces={"application/json"}) 
    @ResponseBody
    public List<Review> getReviewListByRestaurant(@PathVariable Long restaurantId){
      List<Review> restaurantList = this.reviewRepository.getAllByRestaurantId(restaurantId);
      if(!restaurantList.isEmpty()){
        return restaurantList; 
      }
      throw new RestaurantNotFoundException();
    }
    /* Reviews section requests */

    /* Restaurant section requests */    
    @PostMapping(value="/addrestaurant", consumes = {"application/json"}, produces={"application/json"})
    @ResponseBody
    public Restaurant createNewRestaurant(@RequestBody Restaurant restaurant){
      boolean ifExists = this.restaurantRepository.existsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode()); 
        if(!ifExists){
          return this.restaurantRepository.save(restaurant);
        }
        return restaurant;
    }

    @GetMapping(value="/restaurant/{id}", produces={"application/json"})
    @ResponseBody
    public Restaurant getRestaurantDetails(@PathVariable("id") Long id){
      Optional <Restaurant> restaurantOptional = this.restaurantRepository.findById(id);
      if(!restaurantOptional.isPresent()){
        throw new RestaurantNotFoundException();
      }
      return restaurantOptional.get();
    }

    @GetMapping(value="/restaurant/search", produces={"application/json"})
    public List<Restaurant> searchRestaurantForAllergy(@RequestParam Integer zipcode ){
      List<Restaurant> restaurantList = this.restaurantRepository.getByZipCodeAndOverallScoreIsGreaterThanOrderByNameDesc(zipcode, 0);
      if(!restaurantList.isEmpty()){
          return restaurantList;
      }
      throw new RestaurantNotFoundByZipCodeException();
    }
   /* End of Restaurant section requests */  
   
   /*exception handling begin */

  @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="User not added")  // 404
  public class ReviewerNotAddedException extends RuntimeException {
      // ...
  }
  
  @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No User found")  // 404
 public class ReviewerNotFoundException extends RuntimeException {
     // ...
 }

 @ResponseStatus(value=HttpStatus.FOUND, reason="A user exists by that username.")  // 4
 public class ReviewerFoundException extends RuntimeException {
     // ...
 }

 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No restaurant found for given id")  // 404
 public class RestaurantNotFoundException extends RuntimeException {
     // ...
 }

 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No restaurant found for given zipcode")  // 404
 public class RestaurantNotFoundByZipCodeException extends RuntimeException {
     // ...
 }


 @ResponseStatus(value=HttpStatus.FOUND, reason="A restaurant found for given name and zipcode")  
 public class RestaurantFoundException extends RuntimeException {
     // ...
 }

 @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="A restaurant not added")  
 public class RestaurantNotAddedException extends RuntimeException {
     // ...
 }



 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Review not found by that id.")  // 404
 public class ReviewNotFoundException extends RuntimeException {
     // ...
 }

 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Approved reviews found for that restaurant.")  // 404
 public class NoApprovedReviewFoundException extends RuntimeException {
     // ...
 }


 @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Review not added. Please review the request.")  // 400
 public class ReviewNotUpdatedException extends RuntimeException {
     // ...
 }


}
