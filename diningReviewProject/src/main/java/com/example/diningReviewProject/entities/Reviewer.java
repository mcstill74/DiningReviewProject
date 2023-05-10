package com.example.diningReviewProject.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Reviewer {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String name;
    private String city;
    private String state;
    private String zipCode;
    private Boolean peanutAllergy;
    private Boolean eggAllergy;
    private Boolean dairyAllergy;

    
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUserName(){
        return userName;
    }
    
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public String getCity(){
        return city;
    }
    
    public void setCity(String city){
        this.city = city;
    }

    public String getState(){
        return state;
    }
    
    public void setState(String state){
        this.state = state;
    }

    public String getZipCode(){
        return zipCode;
    }
    
    public void setZipCode(String zipCode){
        this.zipCode = zipCode;
    }

    public Boolean getPeanutAllergy(){
        return peanutAllergy;
    }
    
    public void setPeanutAllergy(Boolean peanutAllergy){
        this.peanutAllergy = peanutAllergy;
    }
    public Boolean getEggAllergy(){
        return eggAllergy;
    }
    
    public void setEggAllergy(Boolean eggAllergy){
        this.eggAllergy = eggAllergy;
    }

    public Boolean getDairyAllergy(){
        return dairyAllergy;
    }
    
    public void setDairyAllergy(Boolean dairyAllergy){
        this.dairyAllergy = dairyAllergy;
    }

    public Reviewer(){

    }

    public Reviewer(String userName, String name, String city, String state, String zipCode, Boolean peanutAllergy, Boolean eggAllergy, Boolean dairyAllergy){
        this.userName = userName;
        this.name = name;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.peanutAllergy = peanutAllergy;
        this.eggAllergy = eggAllergy;
        this.dairyAllergy = dairyAllergy;
    }

}
