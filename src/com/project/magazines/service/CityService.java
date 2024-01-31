package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.helper.QueryBuilder;

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
        List<Map<String, Object>> cities = dbConnection.executeSelectQuery(
                QueryBuilder.query()
                        .select(
                                "city.*",
                                "country.id as country_id",
                                "country.name as country_name")
                        .from("city")
                        .join("country", "city.country_id", "country.id")
                        .when(search != null, query ->
                                query.where("city.name", "LIKE", "%" + search + "%")
                                        .orWhere("country.name", "LIKE", "%" + search + "%"))
                        .toString());

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

    public boolean save(City city) {
        if (city == null) {
            System.out.println("City is null!");
            return false;
        }

        Country country = countryService.getById(city.getCountry().getId());

        if (country == null) {
            System.out.println("Country with id " + city.getCountry().getId() + " does not exist.");
            return false;
        }

        if (checkIfCityExists(city)) {
            System.out.println("City already exists!");
            return false;
        }

        if (city.getId() == null)
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("city", Map.of("name", city.getName(), "country_id", country.getId()))
                    .toString());
        else
            dbConnection.executeSaveQuery(
                    QueryBuilder.query()
                            .update("city", Map.of("name", city.getName(), "country_id", country.getId()))
                            .where("id", "=", city.getId())
                            .toString());

        return true;
    }

    public City getById(Long id) {
        if (id == null)
            return null;

        Map<String, Object> result =
                dbConnection.executeFindByIdQuery(
                        QueryBuilder.query()
                                .select(
                                        "city.*",
                                        "country.id as country_id",
                                        "country.name as country_name")
                                .from("city")
                                .join("country", "city.country_id", "country.id")
                                .where("city.id", "=", id)
                                .toString());

        return result.isEmpty() ? null : mapToCity(result);
    }

    public Long findId(City city) {
        if (city == null)
            return -1L;

        return dbConnection.executeFindIdQuery(
                QueryBuilder.query()
                        .select("id")
                        .from("city")
                        .where("name", "=", city.getName())
                        .where("country_id", "=", countryService.findId(city.getCountry()))
                        .toString());
    }

    public boolean delete(Long id) {
        if (id == null) {
            System.out.println("Id is null!");
            return false;
        }

        if (dbConnection.executeExistsQuery(
                QueryBuilder.query()
                        .exists(
                                QueryBuilder.query()
                                        .select()
                                        .from("employee")
                                        .where("city_id", "=", id)
                                , "result")
                        .toString())) {
            System.out.println("Cannot delete city with id " + id + " because it is used by an employee.");
        }

        if (dbConnection.executeExistsQuery(
                QueryBuilder.query()
                        .exists(
                                QueryBuilder.query()
                                        .select()
                                        .from("salestore")
                                        .where("city_id", "=", id)
                                , "result")
                        .toString())) {
            System.out.println("Cannot delete city with id " + id + " because it is used by a salestore.");
        }

        dbConnection.executeDeleteQuery(
                QueryBuilder.query()
                        .delete("city")
                        .where("id", "=", id)
                        .toString());

        return true;
    }

    private boolean checkIfCityExists(City city) {
        return city != null && dbConnection.executeExistsQuery(
                QueryBuilder.query()
                        .exists(
                                QueryBuilder.query()
                                        .select()
                                        .from("city")
                                        .where("name", "=", city.getName())
                                        .where("country_id", "=", city.getCountry().getId())
                                        .when(city.getId() != null,
                                                query -> query.where("id", "<>", city.getId()))
                                , "result")
                        .toString());
    }
}
