package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Area;
import com.project.magazines.enumeration.LogicalOperator;
import com.project.magazines.helper.Condition;
import com.project.magazines.helper.QueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AreaService {

    private final DatabaseConnection dbConnection;

    public AreaService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    /*
    public List<Area> getAll() {
        return getAll(null);
    }

    public List<Area> getAll(String search) {
        List<Condition> conditions = (search == null) ? null : List.of(
                new Condition(LogicalOperator.WHERE, "area.name", "LIKE", "%" + search + "%")
        );

        return dbConnection.select("area", null, conditions, null)
                .stream()
                .map(area -> new Area((Long) area.get("id"), (String) area.get("name")))
                .toList();
    }*/

    public boolean create(Area area) {
        if (area == null) {
            System.out.println("Area is null!");
            return false;
        }

        if (checkIfAreaExists(area)) {
            System.out.println("Area already exists!");
            return false;
        }

        return dbConnection.executeQuery(QueryBuilder.query()
                .insert("area", Map.of("name", area.getName()))
                .toString(), Boolean.class);
    }

  /*  public boolean update(Area area, Long id) {
        if (area == null || id == null) {
            System.out.println("Area or id is null!");
            return false;
        }

        if (checkIfAreaExists(area, id)) {
            System.out.println("Area already exists!");
            return false;
        }


        return dbConnection.update("area", Map.of("name", area.getName()), id);
    }

    public Area getById(Long id) {
        if (id == null)
            return null;

        List<Map<String, Object>> areas = dbConnection.select("area", null,
                List.of(new Condition(LogicalOperator.WHERE, "id", "=", id)), null);

        if (areas.isEmpty())
            return null;

        return new Area((Long) areas.get(0).get("id"), (String) areas.get(0).get("name"));
    }

    public boolean delete(Long id) {
        return id != null && dbConnection.delete("area", id);
    }

    public Long findId(Area area) {
        if (area == null)
            return -1L;

        return dbConnection.findSingleIdByColumn("area", "name", area.getName());
    }*/

    private boolean checkIfAreaExists(Area area) {
        return checkIfAreaExists(area, null);
    }

    private boolean checkIfAreaExists(Area area, Long id)  {
        if (area == null) {
            System.out.println("Area is null!");
            return false;
        }

        return dbConnection.executeQuery(QueryBuilder.query()
                .exists(
                        QueryBuilder.query()
                                .select()
                                .from("area")
                                .when(id != null, query -> query.where("id", "!=", id))
                                .where("name", area.getName()), "result")
                .toString(), Boolean.class);
    }
}
