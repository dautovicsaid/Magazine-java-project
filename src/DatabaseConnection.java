import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseConnection {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
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

    public boolean insert(String table, Map<String, Object> parameters) {
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

    public boolean update(String table, Map<String, Object> parameters, int id) {
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

    public boolean delete(String table, int id) {
        String sql = buildBasicDeleteQuery(table, id);

        try (Connection connection = open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleting from the database.");
            throw new RuntimeException(e);
        }
    }

    public boolean findByColumn(String table, String column, String value) {
        String sql = "SELECT * FROM " + table + " WHERE " + table + "." + column + " = ?";

        try (Connection connection = open();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            System.out.println("Error finding by column in the database.");
            throw new RuntimeException(e);
        }
    }

    public ResultSet select(String tableName, List<String> columns, List<Condition> conditions, List<Join> joins) {
        try (Connection connection = open()) {
            String sql = buildSelectQuery(tableName, columns, conditions, joins);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, conditions);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            System.out.println("Error executing SELECT query.");
            throw new RuntimeException(e);
        }
    }

    private String buildSelectQuery(String tableName, List<String> columns, List<Condition> conditions, List<Join> joins) {
        StringBuilder sql = new StringBuilder("SELECT ");

        sql.append((columns == null || columns.isEmpty()) ? "*" : String.join(", ", columns));
        sql.append(" FROM ").append(tableName);

        if (joins != null && !joins.isEmpty()) {
            for (Join join : joins) {
                sql.append(" JOIN ")
                        .append(join.getJoinTableName())
                        .append(" ON ")
                        .append(tableName).append(".").append(join.getSourceColumn())
                        .append(" = ")
                        .append(join.getJoinTableName()).append(".").append(join.getTargetColumn());
            }
        }

        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ")
                    .append(conditions.stream()
                            .map(condition -> String.format("%s %s ?", condition.getColumnName(), condition.getComparator()))
                            .collect(Collectors.joining(" AND ")));
        }

        return sql.toString();
    }

    private void setParameters(PreparedStatement preparedStatement, List<Condition> conditions) throws SQLException {
        int parameterIndex = 1;

        if (conditions != null && !conditions.isEmpty()) {
            for (Condition condition : conditions) {
                preparedStatement.setObject(parameterIndex++, condition.getValue());
            }
        }
    }

    private void setParameters(PreparedStatement preparedStatement, Map<String, Object> parameters) throws SQLException {
        int parameterIndex = 1;

        for (Object value : parameters.values()) {
            preparedStatement.setObject(parameterIndex++, value);
        }
    }

    private String buildBasicUpdateQuery(String table, String columns, int id) {
        return "UPDATE " + table + " SET " + columns + " WHERE " + table + ".id = " + id;
    }

    private String buildBasicDeleteQuery(String table, int id) {
        return "DELETE FROM " + table + " WHERE " + table + ".id = " + id;
    }
}
