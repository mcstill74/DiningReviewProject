package com.example.diningReviewProject.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;

@Entity
public class Restaurant{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String city;
    private String state;
    private Integer zipCode;
    private String cuisine;
    private Integer overallScore;

    //tracks scores
    private int egg;
    private int dairy;
    private int peanut;

    //Keeps track of quantity inputs
    private int eggCount = 0;
    private int peanutCount = 0;
    private int dairyCount = 0;
    

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
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

    public Integer getZipCode(){
        return zipCode;
    }

    public void setZipCode(Integer zipCode){
        this.zipCode = zipCode;
    }

    public String getCuisine(){
        return cuisine;
    }

    public void setCuisine(String cuisine){
        this.cuisine = cuisine;
    }

    public Integer getEgg(){
        if(egg > 0)
            return this.egg/this.eggCount;
        else 
            return 0;
    }

    public void setEgg(Integer egg){
        setEggCount(this.getEggCount() + 1);
        this.egg += egg;
    }

    public Integer getPeanut(){
        if(this.peanut > 0)
            return this.peanut/this.getPeanutCount();
        else
            return 0;
    }

    public void setPeanut(Integer peanut){
        setPeanutCount(this.getPeanutCount() + 1);
        this.peanut += peanut;
    }

    public Integer getDairy(){
        if(this.dairy > 0)
            return this.dairy/this.getDairyCount();
        else
            return 0;
    }

    public void setDairy(Integer dairy){
        setDairyCount(this.getDairyCount() + 1);
        this.dairy += dairy;
    }

    private Integer getEggCount(){
        return this.eggCount;
    }

    private void setEggCount(Integer count){
        this.eggCount = count;
    }

    private Integer getPeanutCount(){
        return this.peanutCount;
    }

    private void setPeanutCount(Integer count){
        this.peanutCount = count;
    }

    private Integer getDairyCount(){
        return this.dairyCount;
    }

    private void setDairyCount(Integer count){
        this.dairyCount = count;
    }


    public Integer getOverallScore(){
        if(this.overallScore > 0)
            return this.overallScore;
        else
            return 0;
    }

     public void setOverallScore(Integer overallScore){
        this.overallScore = overallScore;
    }
    
    public Restaurant(){
        this.eggCount = 0;
        this.peanutCount = 0;
        this.dairyCount = 0;
    }

    public Restaurant(String name, String address, String city, String state, Integer zipCode, String cuisine, int egg, int peanut, int dairy, int eggCount, int peanutCount, int dairyCount, int overAllScore){
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.cuisine = cuisine; 
        this.egg = egg;
        this.peanut = peanut;
        this.dairy = dairy;
        
        this.overallScore = overAllScore;
        this.eggCount = eggCount;
        this.peanutCount = peanutCount;
        this.dairyCount = dairyCount;
    }
}
