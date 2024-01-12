package com.project.magazines.entity;
public class City {
    private Long id;
    private String name;
    private Country country;

    public City(String name, Country country) {
        this.id = null;
        this.name = name;
        this.country = country;
    }

    public City(Long id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        this.id = newId;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setCountry(Country newCountry) {
        this.country = newCountry;
    }

    @Override
    public String toString() {
        return """
                {
                    "id": %d,
                    "name": "%s",
                    "country": %s
                }""".formatted(id, name, country);
    }
}
