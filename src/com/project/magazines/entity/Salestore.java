package com.project.magazines.entity;

public class Salestore {

    private Long id;
    private String name;
    private String address;
    private int number;
    private City city;

    public Salestore(Long id,String name, String address, int number, City city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.number = number;
        this.city = city;
    }

    public Salestore(String name, String address, int number, City city) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
