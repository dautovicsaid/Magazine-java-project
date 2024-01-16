package com.project.magazines.entity;

import java.sql.Date;

public class Magazine {

    private Long id;
    private String name;
    private Date releaseDate;
    private int releaseNumber;
    private int price;
    private int totalPrints;

    public Magazine(Long id, String name, Date releaseDate, int releaseNumber, int price, int totalPrints) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.releaseNumber = releaseNumber;
        this.price = price;
        this.totalPrints = totalPrints;
    }

    public Magazine(String name, Date releaseDate, int releaseNumber, int price, int totalPrints) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.releaseNumber = releaseNumber;
        this.price = price;
        this.totalPrints = totalPrints;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public int getReleaseNumber() {
        return releaseNumber;
    }

    public int getPrice() {
        return price;
    }

    public int getTotalPrints() {
        return totalPrints;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setReleaseNumber(int releaseNumber) {
        this.releaseNumber = releaseNumber;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTotalPrints(int totalPrints) {
        this.totalPrints = totalPrints;
    }

    @Override
    public String toString() {
        return "Magazine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", releaseDate=" + releaseDate +
                ", releaseNumber=" + releaseNumber +
                ", price=" + price +
                ", totalPrints=" + totalPrints +
                '}';
    }

}
