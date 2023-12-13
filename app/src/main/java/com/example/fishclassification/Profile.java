package com.example.fishclassification;

public class Profile {
    public String name;
    public String age;
    public String city;
    public String favoriteFish;

    public Profile(String name, String age, String city, String favoriteFish) {
        this.name = name;
        this.age = age;
        this.city = city;
        this.favoriteFish = favoriteFish;
    }

    public Profile() {
        // Default constructor required for Firestore serialization
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFavoriteFish() {
        return favoriteFish;
    }

    public void setFavoriteFish(String favoriteFish) {
        this.favoriteFish = favoriteFish;
    }
}
