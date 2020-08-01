package com.rajumia.fotomela;

public class SearchViewModel {

    private String username,city;

    // Empty Constructor for Firebase
    private SearchViewModel(){};

    // Constructor for getting data
    private SearchViewModel(String username,String city)
    {
        this.username = username;
        this.city = city;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
