package com.example.diningReviewProject.entities;



public class AdminReview{

    private Review reviewObject;

    public Review getReviewObject(){
        return reviewObject;
    }

    public void setReviewObject(Review review){
        this.reviewObject = review;
    }
    
    public AdminReview(){
    }

    public AdminReview (Review review){
        this.reviewObject  = review;
        this.reviewSubmissionForApproval();
    }

    private void reviewSubmissionForApproval(){
        if(!this.reviewObject.getSubmitter().isEmpty() && this.reviewObject.getRestaurantId() != 0)
        {
            this.reviewObject.setStatus(ReviewStatus.PENDING); 
        }
        else{
            this.reviewObject.setStatus(ReviewStatus.REJECT);
        }
    }
}