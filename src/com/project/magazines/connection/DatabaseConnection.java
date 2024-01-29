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

    public boolean close(Connection connection) {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error closing connection to database.");
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> executeSelectQuery(String query) {
        if (!query.trim().toLowerCase().startsWith("select"))
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
        if (!query.trim().toLowerCase().startsWith("select exists"))
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
        if (!query.trim().toLowerCase().startsWith("insert") && !query.trim().toLowerCase().startsWith("update"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error executing SAVE query.");
            throw new RuntimeException(e);
        }
    }

    public void executeDeleteQuery(String query) {
        if (!query.trim().toLowerCase().startsWith("delete"))
            throw new IllegalArgumentException("Invalid query type");

        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error executing DELETE query.");
            throw new RuntimeException(e);
        }
    }

    public Long executeFindIdQuery(String query) {
        if (!query.trim().toLowerCase().startsWith("select id from"))
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

    public Map<String, Object> executeFindByIdQuery(String query) {
        if (!query.trim().toLowerCase().startsWith("select"))
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

    /*public boolean insert(String table, Map<String, Object> parameters) {
        String columns = String.join(", ", parameters.keySet());
        String values = String.join(", ", Collections.nCopies(parameters.size(), "?"));

        String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ")";

        try (Connection connection = open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setParameters(preparedStatement, parameters);

            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error inserting into the database.");
            throw new RuntimeException(e);
        }
    }

    public boolean update(String table, Map<String, Object> parameters, Long id) {
        String columns = String.join(" = ?, ", parameters.keySet()) + " = ?";
        String sql = buildBasicUpdateQuery(table, columns, id);

        try (Connection connection = open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setParameters(preparedStatement, parameters);

            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updating the database.");
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String table, Long id) {
        String sql = buildBasicDeleteQuery(table, id);

        try (Connection connection = open()) {
            connection.createStatement().executeUpdate(sql);
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleting from the database.");
            throw new RuntimeException(e);
        }
    }

    public List<Map<String, Object>> findById(String table, Long id) {
        if (id == null || table == null)
            return null;

        String sql = "SELECT * FROM " + table + " WHERE " + table + ".id = " + id;

        try (Connection connection = open()) {
            return processResultSet(connection.createStatement().executeQuery(sql));
        } catch (SQLException e) {
            System.out.println("Error finding by id in the database.");
            throw new RuntimeException(e);
        }
    }

    public Long findSingleIdByColumn(String table, String column, String value) {
        String sql = "SELECT id FROM " + table + " WHERE " + table + "." + column + " = '" + value + "'";

        try (Connection connection = open()) {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);

            return resultSet.next() ? resultSet.getLong("id") : -1L;
        } catch (SQLException e) {
            System.out.println("Error finding id by column in the database.");
            throw new RuntimeException(e);
        }
    }

    public List<Long> findIdsByColumn(String table, String column, String value) {
        String sql = "SELECT id FROM " + table + " WHERE " + table + "." + column + " = " + value;

        try (Connection connection = open()) {
            return getIdListFromResultSet(connection.createStatement().executeQuery(sql));

        } catch (SQLException e) {
            System.out.println("Error finding id by column in the database.");
            throw new RuntimeException(e);
        }
    }

    private List<Long> getIdListFromResultSet(ResultSet resultSet) throws SQLException {
        List<Long> ids = new ArrayList<>();
        while (resultSet.next()) {
            ids.add(resultSet.getLong("id"));
        }
        return ids;
    }

    public boolean exists(String table, List<Condition> conditions, List<Join> joins) {
        String sql = buildExistsQuery(table, conditions, joins);

        try (Connection connection = open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            setParameters(preparedStatement, conditions);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getBoolean("result");

        } catch (SQLException e) {
            System.out.println("Error checking if record exists in the database.");
            throw new RuntimeException(e);
        }
    }

  *//*  public List<Map<String, Object>> select(String tableName, List<String> columns) {
        try (Connection connection = open()) {
            String sql = "SELECT " + String.join(", ", columns) + " FROM " + tableName;

            return processResultSet(connection.createStatement().executeQuery(sql));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*//*

    public List<Map<String, Object>> select(String tableName, List<String> columns) {
       return select(tableName, columns, null, null, null);
    }

    public List<Map<String, Object>> select(String tableName, List<String> columns, List<Condition> conditions) {
        return select(tableName, columns, conditions, null, null);
    }

    public List<Map<String, Object>> select(String tableName, List<String> columns, List<Join> joins) {
        return select(tableName, columns, null, joins, null);
    }

    public List<Map<String, Object>> select(String tableName, List<String> columns,
                                            List<Condition> conditions, List<Join> joins,
                                            List<QueryOption> queryOptions) {
        try (Connection connection = open()) {
            String sql = buildSelectQuery(tableName, columns, conditions, joins, queryOptions);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, conditions);
            ResultSet resultSet = preparedStatement.executeQuery();

            return processResultSet(resultSet);

        } catch (SQLException e) {
            System.out.println("Error executing SELECT query.");
            throw new RuntimeException(e);
        }
    }

    private String buildExistsQuery(String tableName, List<Condition> conditions, List<Join> joins) {
        StringBuilder sql = new StringBuilder("SELECT EXISTS (SELECT 1 FROM ").append(tableName);
        appendJoins(sql, joins, tableName);
        appendConditions(sql, conditions);
        sql.append(") AS result");
        return sql.toString();
    }

    private String buildSelectQuery(String tableName, List<String> columns,
                                    List<Condition> conditions, List<Join> joins,
                                    List<QueryOption> queryOptions) {
        StringBuilder sql = new StringBuilder("SELECT ");
        appendColumns(sql, columns);
        sql.append(" FROM ").append(tableName);
        appendJoins(sql, joins, tableName);
        appendConditions(sql, conditions);
        appendQueryOptions(sql, queryOptions);
        return sql.toString();
    }

    private void appendColumns(StringBuilder sql, List<String> columns) {
        sql.append((columns == null || columns.isEmpty()) ? "*" : String.join(", ", columns));
    }

    private void appendJoins(StringBuilder sql, List<Join> joins, String tableName) {
        if (joins == null || joins.isEmpty()) {
            return;
        }

        for (Join join : joins) {
            String targetTable = join.targetTableName() == null ? tableName : join.targetTableName();
            sql.append(" ")
                    .append(join.joinType())
                    .append(" JOIN ")
                    .append(join.joinTableName())
                    .append(" ON ")
                    .append(targetTable).append(".").append(join.sourceColumn())
                    .append(" = ")
                    .append(join.joinTableName()).append(".").append(join.targetColumn());
        }
    }

    private void appendConditions(StringBuilder sql, List<Condition> conditions) {
        if (conditions != null && !conditions.isEmpty()) {

            if (conditions.get(0).logicalOperator() != LogicalOperator.WHERE)
                throw new RuntimeException("First condition must be WHERE");

            sql.append(conditions.stream()
                    .map(condition -> String.format(" %s %s %s ?", condition.logicalOperator(), condition.columnName(), condition.comparator()))
                    .collect(Collectors.joining(" ")));
        }
    }

    private void appendQueryOptions(StringBuilder sql, List<QueryOption> queryOptions) {
        if (queryOptions == null || queryOptions.isEmpty()) {
            return;
        }

        validateSingleOption(queryOptions, QueryOptionType.LIMIT, "LIMIT");
        validateSingleOption(queryOptions, QueryOptionType.OFFSET, "OFFSET");

        appendOption(sql, queryOptions, QueryOptionType.GROUP_BY, "GROUP BY ", QueryOption::getColumnName);
        appendOption(sql, queryOptions, QueryOptionType.ORDER_BY, "ORDER BY ", queryOption ->
                queryOption.getColumnName() + " " + queryOption.getOrderByDirection());
        appendOption(sql, queryOptions, QueryOptionType.LIMIT, "LIMIT ", queryOption ->
                String.valueOf(queryOption.getLimitCount()));
        appendOption(sql, queryOptions, QueryOptionType.OFFSET, "OFFSET ", queryOption ->
                String.valueOf(queryOption.getOffsetCount()));
    }

    private void validateSingleOption(List<QueryOption> queryOptions, QueryOptionType type, String typeName) {
        long count = queryOptions.stream().filter(queryOption -> queryOption.getType() == type).count();
        if (count > 1) {
            throw new RuntimeException("Only one " + typeName + " option is allowed.");
        }
    }

    private void appendOption(StringBuilder sql, List<QueryOption> queryOptions, QueryOptionType type, String keyword,
                              Function<QueryOption, String> mapper) {
        String optionString = queryOptions.stream()
                .filter(queryOption -> queryOption.getType() == type)
                .map(mapper)
                .collect(Collectors.joining(", ", keyword, ""));

        if (!optionString.equalsIgnoreCase(keyword)) {
            sql.append(optionString);
        }
    }

    private void setParameters(PreparedStatement preparedStatement, List<Condition> conditions) throws SQLException {
        int parameterIndex = 1;

        if (conditions != null && !conditions.isEmpty()) {
            for (Condition condition : conditions) {
                preparedStatement.setObject(parameterIndex++, condition.value());
            }
        }
    }

    private void setParameters(PreparedStatement preparedStatement, Map<String, Object> parameters) throws SQLException {
        int parameterIndex = 1;

        for (Object value : parameters.values()) {
            preparedStatement.setObject(parameterIndex++, value);
        }
    }

    private String buildBasicUpdateQuery(String table, String columns, Long id) {
        return "UPDATE " + table + " SET " + columns + " WHERE " + table + ".id = " + id;
    }

    private String buildBasicDeleteQuery(String table, Long id) {
        return "DELETE FROM " + table + " WHERE " + table + ".id = " + id;
    }

    private List<Map<String, Object>> processResultSet(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object columnValue = resultSet.getObject(i);
                row.put(columnName, columnValue);
            }
            resultList.add(row);
        }

        return resultList;
    }*/
}
