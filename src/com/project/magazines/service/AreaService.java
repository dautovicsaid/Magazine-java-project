package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Area;

import java.util.List;
import java.util.Map;

public class AreaService {

    private final DatabaseConnection dbConnection;

    public AreaService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<Area> getAll() {
        List<Map<String, Object>> areas = dbConnection.select("area", null, null, null);

        return areas.stream()
                .map(area -> new Area((Long) area.get("id"), (String) area.get("name")))
                .toList();
    }

    public boolean create(Area area) {
        return area != null && dbConnection.insert("area", Map.of("name", area.getName()));
    }

    public boolean update(Area area, Long id) {
        return area != null && dbConnection.update("area", Map.of("name", area.getName()), id);
    }

    public boolean delete(Long id) {
        return id != null && dbConnection.delete("area", id);
    }

    public Long findId(Area area) {
        if (area == null)
            return -1L;

        return dbConnection.findSingleIdByColumn("area", "name", area.getName());
    }
}
