package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Area;
import com.project.magazines.helper.QueryBuilder;

import java.util.List;
import java.util.Map;

public class AreaService {

    private final DatabaseConnection dbConnection;

    public AreaService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }


    public List<Area> getAll() {
        return getAll(null);
    }

    public List<Area> getAll(String search) {

        List<Map<String, Object>> result = dbConnection.executeSelectQuery(
                QueryBuilder.query()
                        .select()
                        .from("area")
                        .when(search != null, query ->
                                query.where("name", "LIKE", "%" + search + "%"))
                        .toString());

        return result.stream()
                .map(area -> new Area((Long) area.get("id"), (String) area.get("name")))
                .toList();
    }

    public boolean save(Area area) {
        if (area == null) {
            System.out.println("Area is null!");
            return false;
        }

        if (checkIfAreaExists(area, area.getId())) {
            System.out.println("Area already exists!");
            return false;
        }

        if (area.getId() == null)
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("area", Map.of("name", area.getName()))
                    .toString());
        else
            dbConnection.executeSaveQuery(
                    QueryBuilder.query()
                            .update("area", Map.of("name", area.getName()))
                            .where("id", "=", area.getId())
                            .toString());

        return true;
    }

    public Area getById(Long id) {
        if (id == null)
            return null;

        Map<String, Object> result = dbConnection.executeFindByIdQuery(
                QueryBuilder.query()
                        .select()
                        .from("area")
                        .where("id", "=", id)
                        .toString());

        return result.isEmpty() ? null : new Area(id, (String) result.get("name"));
    }

    public boolean delete(Long id) {
        if (id == null) {
            System.out.println("Id is null!");
            return false;
        }

        if (dbConnection.executeExistsQuery(
                QueryBuilder.query()
                        .exists(QueryBuilder.query()
                                        .select()
                                        .from("employee_area")
                                        .where("area_id", "=", id), "result")
                        .toString())) {
            System.out.println("Area is associated with an employee!");
            return false;
        }

        dbConnection.executeDeleteQuery(
                QueryBuilder.query()
                        .delete("area")
                        .where("id", "=", id)
                        .toString());

        return true;
    }

    public Long findId(Area area) {
        if (area == null)
            return -1L;

        return dbConnection.executeFindIdQuery(
                QueryBuilder.query()
                        .select("id")
                        .from("area")
                        .where("name", "=", area.getName())
                        .toString());
    }

    private boolean checkIfAreaExists(Area area, Long id) {
        if (area == null) {
            System.out.println("Area is null!");
            return false;
        }

        return dbConnection.executeExistsQuery(QueryBuilder.query()
                .exists(
                        QueryBuilder.query()
                                .select()
                                .from("area")
                                .where("id", "<>", id)
                                .where("name", area.getName()), "result")
                .toString());
    }
}
