/*
package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Country;
import com.project.magazines.helper.Condition;
import com.project.magazines.enumeration.LogicalOperator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CountryService {

    private final DatabaseConnection databaseConnection;

    public CountryService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<Country> getAll() {
        List<Map<String, Object>> countries =
                databaseConnection.select("country", null, null, null);

        return countries.stream()
                .map(country -> new Country((Long) country.get("id"), (String) country.get("name")))
                .toList();
    }

    public List<Country> getAll(String name) {
        List<Map<String, Object>> countries = databaseConnection.select(
                "country", null,
                List.of(new Condition(
                        LogicalOperator.WHERE,
                        "name",
                        "LIKE",
                        "%" + name + "%")),
                null);

        return countries.stream()
                .map(country -> new Country((Long) country.get("id"), (String) country.get("name")))
                .toList();
    }

    public boolean create(Country country) {
        if (country == null) {
            System.out.println("Country is null.");
            return false;
        }

        if (checkIfCountryExists(country)) {
            System.out.println("Country with name " + country.getName() + " already exists.");
            return false;
        }

        return databaseConnection.insert("country", Map.of("name", country.getName()));
    }

    public boolean update(Country country, Long id) {

        if (country == null) {
            System.out.println("Country is null.");
            return false;
        }

        if (checkIfCountryExists(country, id)) {
            System.out.println("Country with name " + country.getName() + " already exists.");
            return false;
        }

        return databaseConnection.update("country", Map.of("name", country.getName()), id);
    }

    public boolean delete(Long id) {
        if (id == null)
            return false;

        if (databaseConnection.exists("city",
                List.of(new Condition(LogicalOperator.WHERE, "country_id", "=", id)), null)) {
            System.out.println("Cannot delete country with id " + id + " because it has cities associated with it.");
            return false;
        }
        return databaseConnection.delete("country", id);
    }

    public Long findId(Country country) {
        if (country == null)
            return -1L;

        return databaseConnection.findSingleIdByColumn("country", "name", country.getName());
    }

    private boolean checkIfCountryExists(Country country) {
        return databaseConnection.exists("country",
                Collections.singletonList(new Condition(LogicalOperator.WHERE,
                        "name",
                        "=",
                        country.getName())),
                null);
    }

    private boolean checkIfCountryExists(Country country, Long id) {
        return databaseConnection.exists("country",
                List.of(new Condition(LogicalOperator.WHERE,
                                "name",
                                "=",
                                country.getName()),
                        new Condition(LogicalOperator.AND,
                                "id",
                                "!=",
                                id)),
                null);
    }
}
*/
