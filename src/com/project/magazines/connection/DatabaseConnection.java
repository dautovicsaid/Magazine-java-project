package com.project.magazines.connection;


import java.sql.*;
import java.util.*;


public class DatabaseConnection {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnection(String username, String password, String host, String port, String database) {
        super();
        this.username = username;
        this.password = password;
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

    public Connection open() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Error opening connection to database.");
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> executeSelectQuery(String query) {
        if (query == null
                || query.isEmpty()
                || !query.trim().toLowerCase().startsWith("select"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {

                return resultSet.next() ? resultSetToList(resultSet) : Collections.emptyList();
            }
        } catch (SQLException e) {
            System.out.println("Error executing SELECT query.");
            throw new RuntimeException(e);
        }
    }

    public boolean executeExistsQuery(String query) {
        if (query == null
                || query.isEmpty()
                || !query.trim().toLowerCase().startsWith("select exists"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next() && resultSet.getBoolean("result");
            }
        } catch (SQLException e) {
            System.out.println("Error executing EXISTS query.");
            throw new RuntimeException(e);
        }
    }

    public void executeSaveQuery(String query) {
        if (query == null
                || query.isEmpty()
                || (!query.trim().toLowerCase().startsWith("insert") && !query.trim().toLowerCase().startsWith("update")))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error executing SAVE query.");
            throw new RuntimeException(e);
        }
    }

    public void executeDeleteQuery(String query) {
        if (query == null
                || query.isEmpty()
                || !query.trim().toLowerCase().startsWith("delete"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error executing DELETE query.");
            throw new RuntimeException(e);
        }
    }

    public Long executeFindIdQuery(String query) {
        if (query == null
                || query.isEmpty()
                || !query.trim().toLowerCase().startsWith("select id from"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next() ? resultSet.getLong("id") : -1L;
            }
        } catch (SQLException e) {
            System.out.println("Error executing FIND ID query.");
            throw new RuntimeException(e);
        }
    }

    public Long executeCountQuery(String query) {
        if (query == null
                || query.isEmpty()
                || !query.trim().toLowerCase().startsWith("select count("))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next() ? resultSet.getLong("count") : -1L;
            }
        } catch (SQLException e) {
            System.out.println("Error executing COUNT query.");
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> executeFindByIdQuery(String query) {
        if (query == null
                || query.isEmpty()
                || !query.trim().toLowerCase().startsWith("select"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                return resultSet.next() ? resultSetToMap(resultSet, metaData, columnCount) : Collections.emptyMap();
            }
        } catch (SQLException e) {
            System.out.println("Error executing FIND BY ID query.");
            throw new RuntimeException(e);
        }

    }

    private Map<String, Object> resultSetToMap(ResultSet resultSet, ResultSetMetaData metaData, int columnCount) throws SQLException {
        Map<String, Object> row = new HashMap<>();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            Object columnValue = resultSet.getObject(i);
            row.put(columnName, columnValue);
        }

        return row;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        do resultList.add(resultSetToMap(resultSet, metaData, columnCount));
        while (resultSet.next());

        return resultList;
    }
}
