package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Country;
import com.project.magazines.helper.QueryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CountryService {

    private final DatabaseConnection dbConnection;

    public CountryService(DatabaseConnection databaseConnection) {
        this.dbConnection = databaseConnection;
    }

    public List<Country> getAll() {
        return getAll(null);
    }

    public List<Country> getAll(String search) {
        List<Map<String, Object>> countries = dbConnection.executeSelectQuery(
                QueryBuilder.query()
                        .select()
                        .from("country")
                        .when(search != null, query ->
                                query.where("name", "LIKE", "%" + search + "%"))
                        .toString());

        return countries.stream()
                .map(country -> new Country((Long) country.get("id"), (String) country.get("name")))
                .toList();
    }

    public boolean save(Country country) {
        if (country == null) {
            System.out.println("Country is null.");
            return false;
        }

        if (country.getName() == null || country.getName().isBlank()) {
            System.out.println("Country name is null or blank.");
            return false;
        }

        if (checkIfCountryExists(country, country.getId())) {
            System.out.println("Country with name " + country.getName() + " already exists.");
            return false;
        }

        if (country.getId() == null)
            dbConnection.executeSaveQuery(
                    QueryBuilder.query()
                            .insert("country", Map.of("name", country.getName()))
                            .toString());
        else
            dbConnection.executeSaveQuery(
                    QueryBuilder.query()
                            .update("country", Map.of("name", country.getName()))
                            .where("id", "=", country.getId())
                            .toString());

        return true;
    }

    public Country getById(Long id) {
        if (id == null)
            return null;

        Map<String, Object> result = dbConnection.executeFindByIdQuery(
                QueryBuilder.query()
                        .select()
                        .from("country")
                        .where("id", "=", id)
                        .toString());

        return result.isEmpty() ? null : new Country(id, (String) result.get("name"));
    }

    public boolean delete(Long id) {
        if (id == null)
            return false;

        if (dbConnection.executeExistsQuery(
                QueryBuilder.query().exists(
                                QueryBuilder.query()
                                        .select()
                                        .from("city")
                                        .where("country_id", "=", id), "result")
                        .toString())) {
            System.out.println("Cannot delete country with id " + id + " because it has cities associated with it.");
            return false;
        }
        dbConnection.executeDeleteQuery(
                QueryBuilder.query()
                        .delete("country")
                        .where("id", "=", id)
                        .toString());

        return true;
    }

    public Long findId(Country country) {
        if (country == null)
            return -1L;

        return dbConnection.executeFindIdQuery(
                QueryBuilder.query()
                        .select("id")
                        .from("country")
                        .where("name", "=", country.getName())
                        .toString());
    }

    public Map<String, Object> getCountryWithHighestNumberOfCities() {
        List<Map<String, Object>> result = dbConnection.executeSelectQuery(QueryBuilder
                .query()
                .select("country.id", "country.name", "COUNT(city.id) AS cities_count")
                .from("country")
                .join("city", "country.id", "city.country_id")
                .groupBy("country.id")
                .orderBy("cities_count", "DESC")
                .limit(1).toString());

        return result.isEmpty() ? Collections.emptyMap() : result.get(0);
    }

    private boolean checkIfCountryExists(Country country, Long id) {
        return dbConnection.executeExistsQuery(
                QueryBuilder.query()
                        .exists(
                                QueryBuilder.query()
                                        .select()
                                        .from("country")
                                        .where("name", "=", country.getName())
                                        .when(id != null, query -> query.where("id", "<>", id)), "result")
                        .toString());
    }
}
