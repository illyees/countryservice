package com.countryservice.demo.beans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "country")
public class Country {
    
    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "name_country")
    private String countryName;
    
    @Column(name = "capital_name")
    private String capitalName;
    
    // Constructors
    public Country() {}
    
    public Country(int id, String countryName, String capitalName) {
        this.id = id;
        this.countryName = countryName;
        this.capitalName = capitalName;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    public String getCapitalName() {
        return capitalName;
    }
    
    public void setCapitalName(String capitalName) {
        this.capitalName = capitalName;
    }
    
    // Optional: toString method for debugging
    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", countryName='" + countryName + '\'' +
                ", capitalName='" + capitalName + '\'' +
                '}';
    }
}