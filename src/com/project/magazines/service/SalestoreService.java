package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.entity.Salestore;
import com.project.magazines.enumeration.JoinType;
import com.project.magazines.enumeration.LogicalOperator;
import com.project.magazines.helper.Condition;
import com.project.magazines.helper.Join;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SalestoreService {

    private final DatabaseConnection dbConnection;

    private final CityService cityService;

    public SalestoreService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.cityService = new CityService(dbConnection);
    }

    public List<Salestore> getAll() {
        return getAll(null, null);
    }

    public List<Salestore> getAll(City city) {
        return getAll(null, city);
    }

    public List<Salestore> getAll(String search, City city) {
        List<Condition> conditions = (search == null && city == null) ? null : List.of(
                new Condition(LogicalOperator.WHERE, "salestore.name", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "salestore.address", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "city.name", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "country.name", "LIKE", "%" + search + "%")
        );

        if (city != null) {
            conditions.add(new Condition(LogicalOperator.AND, "city_id", "=", cityService.findId(city)));
        }

        return getAndMapSalestores(conditions);
    }

    private List<Salestore> getAndMapSalestores(List<Condition> conditions) {
        List<Map<String, Object>> salestores = dbConnection.select("salestore",
                List.of(
                        "salestore.*",
                        "city.name as city_name",
                        "city.country_id",
                        "country.name as country_name"),
                conditions,
                List.of(
                        new Join(JoinType.INNER,
                                "city",
                                "city_id",
                                "id"),
                        new Join(JoinType.INNER,
                                "city",
                                "country",
                                "country_id",
                                "id"
                        )));

        return salestores.stream()
                .map(this::mapToSalestore)
                .toList();
    }

    private Salestore mapToSalestore(Map<String, Object> data) {
        return new Salestore(
                (Long) data.get("id"),
                (String) data.get("name"),
                (String) data.get("address"),
                (int) data.get("number"),
                new City(
                        (Long) data.get("city_id"),
                        (String) data.get("city_name"),
                        new Country(
                                (Long) data.get("country_id"),
                                (String) data.get("country_name"))
                )
        );
    }

    public boolean create(Salestore salestore) {
        if (salestore == null) {
            System.out.println("Salestore is null!");
            return false;
        }

        if (checkIfSalestoreExists(salestore)) {
            System.out.println("Salestore already exists!");
            return false;
        }

        Long cityId = cityService.findId(salestore.getCity());

        return cityId != -1 &&
                dbConnection.insert("salestore", Map.of("name", salestore.getName(),
                        "address", salestore.getAddress(),
                        "city_id", cityId));
    }

    public boolean update(Salestore salestore, Long id) {
        if (salestore == null || id == null) {
            System.out.println("Salestore or id is null!");
            return false;
        }

        if (checkIfSalestoreExists(salestore, id)) {
            System.out.println("Salestore already exists!");
            return false;
        }

        Long cityId = cityService.findId(salestore.getCity());

        return cityId != -1 &&
                dbConnection.update("salestore",
                        Map.of("name", salestore.getName(),
                                "address", salestore.getAddress(),
                                "city_id", cityId), id);
    }

    private boolean checkIfSalestoreExists(Salestore salestore) {
        return dbConnection.exists("salestore", buildConditions(salestore, null), null);
    }

    private boolean checkIfSalestoreExists(Salestore salestore, Long id) {
        return dbConnection.exists("salestore", buildConditions(salestore, id), null);
    }

    private List<Condition> buildConditions(Salestore salestore, Long id) {
        List<Condition> conditions = new ArrayList<>(List.of(
                new Condition(LogicalOperator.WHERE, "name", "=", salestore.getName()),
                new Condition(LogicalOperator.AND, "address", "=", salestore.getAddress()),
                new Condition(LogicalOperator.AND, "city_id", "=", cityService.findId(salestore.getCity()))
        ));

        if (id != null) {
            conditions.add(new Condition(LogicalOperator.AND, "id", "!=", id));
        }

        return conditions;
    }
}
