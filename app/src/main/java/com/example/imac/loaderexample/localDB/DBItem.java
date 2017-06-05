package com.example.imac.loaderexample.localDB;

/**
 * Created by imac on 5/23/17.
 */

public class DBItem {

    private int id,mno;
    private String name,city,country;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMobileNo() {
        return mno;
    }

    public void setMobileNo(int mno) {
        this.mno = mno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
