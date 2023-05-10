package com.example.diningReviewProject.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String submitter;
    private Long restaurantId;
    private Integer peanutScore;
    private Integer eggScore;
    private Integer dairyScore;
    private String comment;
    private ReviewStatus status;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getSubmitter(){
        return submitter;
    }

    public void setSubmitter(String submitter){
        this.submitter = submitter;
    }

    public Long getRestaurantId(){
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId){
        this.restaurantId = restaurantId;
    }

    public Integer getPeanutScore(){
        return peanutScore;
    }

    public void setPeanutScore(Integer peanutScore){
        this.peanutScore = peanutScore;
    }

    public Integer getEggScore(){
        return eggScore;
    }

    public void setEggScore(Integer eggScore){
        this.eggScore = eggScore;
    }

    public Integer getDairyScore(){
        return dairyScore;
    }

    public void setDairyScore(Integer dairyScore){
        this.dairyScore = dairyScore;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public ReviewStatus getStatus(){
        return status;
    }

    public void setStatus(ReviewStatus status){
        this.status = status;
    }

    public Review(){

    }

    public Review(String submitter, Long restaurantId, Integer peanutScore, Integer eggScore, Integer dairyScore, String comment){
        this.submitter = submitter;
        this.restaurantId = restaurantId;
        this.peanutScore = peanutScore;
        this.eggScore = eggScore;
        this.dairyScore = dairyScore;
        this.comment = comment;
    }
}
