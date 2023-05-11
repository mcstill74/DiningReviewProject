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

      try{
        Optional<Reviewer> retrieveUserOpt = this.userRepository.findByUserName(username);
        if(!retrieveUserOpt.isPresent()){
          
          throw new ReviewerNotFoundException();
        }
        return retrieveUserOpt.get();
  
      }
      catch(RuntimeException e){
        throw new ReviewerNotFoundException(e.getMessage());
      }
    }

    @PostMapping(value="/addusers", consumes = {"application/json"}, produces={"application/json"})
    @ResponseBody
    public Reviewer createNewUser(@RequestBody Reviewer user){
      try{
        boolean userExists =  this.userRepository.existsByUserName(user.getUserName()); 
        if(!userExists){
            return this.userRepository.save(user);
        }
        return user;
  
      }
      catch(RuntimeException e){
        throw new ReviewerFoundException(e.getMessage(), e.getCause());
      }

    }

    @PutMapping(value="/updateusers", consumes = "application/json", produces="application/json")
    @ResponseBody
      public Reviewer updateAnExistingUser(@RequestParam String username, @RequestBody Reviewer user){

        try{
          Optional<Reviewer> updateOptional = this.userRepository.findByUserName(username); 
          if(!updateOptional.isPresent()){
            throw new ReviewerNotFoundException(username + " not found in sytem.");
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
        catch(RuntimeException e){
          throw new ReviewerNotFoundException(e.getMessage(), e.getCause());
        }
    }
    /* end of users section */

    /* Reviews section requests */
    @PostMapping(value="/addreview", consumes = "application/json", produces="application/json")
    @ResponseBody
    public Review createNewReview(@RequestBody Review review) {
      try{
        String strName = review.getSubmitter();
        if(!this.userRepository.existsByUserName(strName))
          throw new ReviewerNotFoundException(strName + " not found in sytem.");
        else if(!this.restaurantRepository.existsById(review.getRestaurantId()))
          throw new RestaurantNotFoundException(review.getRestaurantId() + " restaurant not found.");
        AdminReview admin = new AdminReview(review);
        return this.reviewRepository.save(admin.getReviewObject() );

      }
      catch(RuntimeException e){
        throw new ReviewerNotAddedException(e.getMessage(), e.getCause());
      }
    }

    @GetMapping(value="/admin/adminReviewList")
    public List<Review> getPendingReviews(){
      return this.reviewRepository.getAllByStatus(ReviewStatus.PENDING);
    }

    @PutMapping(value="/admin/updatereview/{id}", produces={"application/json"})
    @ResponseBody
    public List<Review> adminUpdateReviewStatus(@PathVariable("id") Long id, @RequestParam String status){
      try{
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
      catch(RuntimeException exception){
        throw new ReviewNotUpdatedException(exception.getMessage(), exception.getCause());
      }
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
      try{
        boolean ifExists = this.restaurantRepository.existsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode()); 
          if(!ifExists){
            return this.restaurantRepository.save(restaurant);
          }
          return restaurant;
      }
      catch(RuntimeException e){
        throw new RestaurantNotAddedException(e.getMessage(), e.getCause());
      }
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
      //
      public ReviewerNotAddedException(){
        super();
      }
      public ReviewerNotAddedException(String message, Throwable cause){
        super(message, cause);
      }
      public ReviewerNotAddedException(String message){
        super(message);
      }
      public ReviewerNotAddedException(Throwable cause){
        super(cause);
      }
  }
  
  @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No User found")  // 404
 public class ReviewerNotFoundException extends RuntimeException {
     // ...
     public ReviewerNotFoundException(){
      super();
    }
    public ReviewerNotFoundException(String message, Throwable cause){
      super(message, cause);
    }
    public ReviewerNotFoundException(String message){
      super(message);
    }
    public ReviewerNotFoundException(Throwable cause){
      super(cause);
    }

 }

 @ResponseStatus(value=HttpStatus.FOUND, reason="A user exists by that username.")  // 4
 public class ReviewerFoundException extends RuntimeException {
     // ...
     public ReviewerFoundException(){
      super();
    }
    public ReviewerFoundException(String message, Throwable cause){
      super(message, cause);
    }
    public ReviewerFoundException(String message){
      super(message);
    }
    public ReviewerFoundException(Throwable cause){
      super(cause);
    } 
 }

 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No restaurant found for given id")  // 404
 public class RestaurantNotFoundException extends RuntimeException {
     // ...
     public RestaurantNotFoundException(){
      super();
    }
    public RestaurantNotFoundException(String message, Throwable cause){
      super(message, cause);
    }
    public RestaurantNotFoundException(String message){
      super(message);
    }
    public RestaurantNotFoundException(Throwable cause){
      super(cause);
    }
 }

 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No restaurant found for given zipcode")  // 404
 public class RestaurantNotFoundByZipCodeException extends RuntimeException {
     // ...
     public RestaurantNotFoundByZipCodeException(){
      super();
    }
    public RestaurantNotFoundByZipCodeException(String message, Throwable cause){
      super(message, cause);
    }
    public RestaurantNotFoundByZipCodeException(String message){
      super(message);
    }
    public RestaurantNotFoundByZipCodeException(Throwable cause){
      super(cause);
    }
 }


 @ResponseStatus(value=HttpStatus.FOUND, reason="A restaurant found for given name and zipcode")  
 public class RestaurantFoundException extends RuntimeException {
     // ...
     public RestaurantFoundException(){
      super();
    }
    public RestaurantFoundException(String message, Throwable cause){
      super(message, cause);
    }
    public RestaurantFoundException(String message){
      super(message);
    }
    public RestaurantFoundException(Throwable cause){
      super(cause);
    }
 }

 @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="A restaurant not added")  
 public class RestaurantNotAddedException extends RuntimeException {
     // ...
     public RestaurantNotAddedException(){
      super();
    }
    public RestaurantNotAddedException(String message, Throwable cause){
      super(message, cause);
    }
    public RestaurantNotAddedException(String message){
      super(message);
    }
    public RestaurantNotAddedException(Throwable cause){
      super(cause);
    }
 }


 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Review not found by that id.")  // 404
 public class ReviewNotFoundException extends RuntimeException {
     // ...
     public ReviewNotFoundException(){
      super();
    }
    public ReviewNotFoundException(String message, Throwable cause){
      super(message, cause);
    }
    public ReviewNotFoundException(String message){
      super(message);
    }
    public ReviewNotFoundException(Throwable cause){
      super(cause);
    }
 }

 @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No Approved reviews found for that restaurant.")  // 404
 public class NoApprovedReviewFoundException extends RuntimeException {
     // ...
     public NoApprovedReviewFoundException(){
      super();
    }
    public NoApprovedReviewFoundException(String message, Throwable cause){
      super(message, cause);
    }
    public NoApprovedReviewFoundException(String message){
      super(message);
    }
    public NoApprovedReviewFoundException(Throwable cause){
      super(cause);
    }
 }

 @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Review not added. Please review the request.")  // 400
 public class ReviewNotUpdatedException extends RuntimeException {
     // ...
     public ReviewNotUpdatedException(){
      super();
    }
    public ReviewNotUpdatedException(String message, Throwable cause){
      super(message, cause);
    }
    public ReviewNotUpdatedException(String message){
      super(message);
    }
    public ReviewNotUpdatedException(Throwable cause){
      super(cause);
    }
 }
}
