package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Salestore;
import com.project.magazines.enumeration.LogicalOperator;
import com.project.magazines.helper.Condition;

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
