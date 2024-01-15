package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.helper.Condition;
import com.project.magazines.helper.Join;
import com.project.magazines.enumeration.JoinType;
import com.project.magazines.enumeration.LogicalOperator;

import java.util.List;
import java.util.Map;

public class CityService {

    private final DatabaseConnection dbConnection;
    private final CountryService countryService;


    public CityService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.countryService = new CountryService(dbConnection);
    }

    public List<City> getAll() {
        return getAll(null);
    }

    public List<City> getAll(String search) {
        List<Condition> conditions = (search == null) ? null : List.of(
                new Condition(LogicalOperator.WHERE, "city.name", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "country.name", "LIKE", "%" + search + "%")
        );

        return getAndMapCities(conditions);
    }

    private List<City> getAndMapCities(List<Condition> conditions) {
        List<Map<String, Object>> cities = dbConnection.select("city",
                List.of(
                        "city.*",
                        "country.name as country_name"),
                conditions,
                List.of(
                        new Join(JoinType.INNER,
                                "country",
                                "country_id",
                                "id")
                ));

        return cities.stream()
                .map(this::mapToCity)
                .toList();
    }

    private City mapToCity(Map<String, Object> cityData) {
        return new City(
                (Long) cityData.get("id"),
                (String) cityData.get("name"),
                new Country(
                        (Long) cityData.get("country_id"),
                        (String) cityData.get("country_name")));
    }

    public boolean create(City city) {
        if (city == null)
            return false;

        if (checkIfCityExists(city)) {
            System.out.println("City with name " + city.getName() + " already exists.");
            return false;
        }

        Long countryId = countryService.findId(city.getCountry());

        return countryId != -1 && dbConnection.insert("city", Map.of("name", city.getName(), "country_id", countryId));
    }

    public boolean update(City city, Long id) {
        if (city == null)
            return false;

        if (checkIfCityExists(city, id)) {
            System.out.println("City with name " + city.getName() + " already exists.");
            return false;
        }

        Long countryId = countryService.findId(city.getCountry());

        return countryId != -1 && dbConnection.update("city", Map.of("name", city.getName(), "country_id", countryId), id);
    }

    public City getById(Long id) {
        if (id == null)
            return null;

        return getAndMapCities(List.of(new Condition(LogicalOperator.WHERE, "city.id", "=", id))).get(0);
    }

    public Long findId(City city) {
        if (city == null)
            return -1L;

        return dbConnection.findSingleIdByColumn("city", "name", city.getName());
    }

    private boolean checkIfCityExists(City city) {
        return dbConnection.exists("city", List.of(
                new Condition(LogicalOperator.WHERE, "name", "=", city.getName()),
                new Condition(LogicalOperator.AND, "country_id", "=", countryService.findId(city.getCountry()))
        ), null);
    }

    private boolean checkIfCityExists(City city, Long id) {
        return dbConnection.exists("city", List.of(
                new Condition(LogicalOperator.WHERE, "name", "=", city.getName()),
                new Condition(LogicalOperator.AND, "country_id", "=", countryService.findId(city.getCountry())),
                new Condition(LogicalOperator.AND, "id", "!=", id)

        ), null);
    }
}
