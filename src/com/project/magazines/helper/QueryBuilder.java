package com.project.magazines.helper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryBuilder {

    private final StringBuilder query;


    private final List<String> parameters = List.of("=", "<", ">", "<=", ">=", "<>", "like", "not like");


    public QueryBuilder() {
        this.query = new StringBuilder();
    }

    public static QueryBuilder query() {
        return new QueryBuilder();
    }

    public QueryBuilder select(String... columns) {
        if (!this.query.toString().isEmpty())
            throw new IllegalArgumentException("Can't add select to existing query");

        if (columns.length == 0) {
            this.query.append("SELECT * ");
        } else {
            this.query.append("SELECT ").append(String.join(", ", columns)).append(" ");
        }
        return this;
    }

    public QueryBuilder exists(QueryBuilder query, String alias) {

        if (!this.query.toString().isEmpty())
            throw new IllegalArgumentException("Can't add exists to existing query");

        this.query.append("SELECT EXISTS (").append(query).append(") ").append("as ").append(alias).append(" ");
        return this;
    }

    public QueryBuilder count(String column) {
        if (!this.query.toString().isEmpty())
            throw new IllegalArgumentException("Can't add count to existing query");

        this.query.append("SELECT COUNT(").append(column).append(") as ").append("count").append(" ");
        return this;
    }

    public QueryBuilder update(String table, Map<String, Object> values) {
        if (table == null || table.isBlank()) {
            throw new IllegalArgumentException("Table can't be null or blank");
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Values can't be empty");
        }

        if (!this.query.toString().isEmpty()) {
            throw new IllegalArgumentException("Can't add UPDATE to an existing query");
        }

        this.query.append("UPDATE ").append(table).append(" SET ");

        values.forEach((key, value) -> this.query.append(key)
                .append(" = ")
                .append(value instanceof String ? "'" + value + "'" : value)
                .append(", "));

        this.query.deleteCharAt(this.query.length() - 2);
        return this;
    }

    public QueryBuilder insert(String table, Map<String, Object> values) {
        if (table == null || table.isBlank())
            throw new IllegalArgumentException("Table can't be null or blank");

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Values can't be empty");
        }

        if (!this.query.toString().isEmpty()) {
            throw new IllegalArgumentException("Can't add INSERT to an existing query");
        }

        this.query.append("INSERT INTO ").append(table).append(" (");

        values.keySet().forEach(key -> this.query.append(key).append(", "));
        this.query.delete(this.query.length() - 2, this.query.length());

        this.query.append(") VALUES (");

        values.values().forEach(value -> {
            if (value instanceof String) {
                this.query.append("'").append(value).append("', ");
            } else {
                this.query.append(value).append(", ");
            }
        });
        this.query.delete(this.query.length() - 2, this.query.length());

        this.query.append(") ");

        return this;
    }

    public QueryBuilder delete(String table) {
        if (!this.query.toString().isEmpty())
            throw new IllegalArgumentException("Can't add delete to existing query");

        this.query.append("DELETE FROM ").append(table).append(" ");
        return this;
    }

    public QueryBuilder from(String table) {
        if (!this.query.toString().startsWith("SELECT") || this.query.toString().contains("FROM"))
            throw new IllegalArgumentException("Can't add from to existing query");

        this.query.append("FROM ").append(table).append(" ");
        return this;
    }

    public QueryBuilder from(QueryBuilder query, String alias) {
        if (!this.query.toString().startsWith("SELECT") || this.query.toString().contains("FROM"))
            throw new IllegalArgumentException("Can't add from to existing query");

        this.query.append("FROM (").append(query).append(") ").append("as ").append(alias).append(" ");
        return this;
    }

    public QueryBuilder join(String table, String firstColumn, String secondColumn) {
        return innerJoin(table, firstColumn, secondColumn);
    }

    private QueryBuilder join(String joinType, String table, String firstColumn, String secondColumn) {
        this.query.append(joinType).append(" JOIN ").append(table).append(" ON ")
                .append(firstColumn).append(" = ").append(secondColumn).append(" ");
        return this;
    }

    public QueryBuilder innerJoin(String table, String firstColumn, String secondColumn) {
        return join("INNER", table, firstColumn, secondColumn);
    }

    public QueryBuilder leftJoin(String table, String firstColumn, String secondColumn) {
        return join("LEFT", table, firstColumn, secondColumn);
    }

    public QueryBuilder rightJoin(String table, String firstColumn, String secondColumn) {
        return join("RIGHT", table, firstColumn, secondColumn);
    }

    public QueryBuilder fullJoin(String table, String firstColumn, String secondColumn) {
        return join("FULL", table, firstColumn, secondColumn);
    }

    public QueryBuilder where(String condition, Object value) {
        return where(condition, "=", value);
    }

    public QueryBuilder where(String condition, String parameter, Object value) {
        if (!parameters.contains(parameter.toLowerCase())) {
            throw new IllegalArgumentException("Invalid parameter");
        }

        if (this.query.toString().endsWith("(")) {
            appendCondition(condition, parameter, value);
        } else if (this.query.toString().contains("WHERE")) {
            this.query.append("AND ");
            appendCondition(condition, parameter, value);
        } else {
            this.query.append("WHERE ");
            appendCondition(condition, parameter, value);
        }

        return this;
    }

    public QueryBuilder where(Function<QueryBuilder, QueryBuilder> function) {
        String condition = function.apply(new QueryBuilder()).toString();

        if (!condition.isEmpty()) {
            if (this.query.toString().contains("WHERE")) {
                this.query.append(" AND (");
            } else {
                this.query.append("WHERE (");
            }
            function.apply(this);
            this.query.append(") ");
        }

        return this;
    }

    public QueryBuilder whereIn(String condition, List<Object> values) {
        String valuesString = values.stream()
                .map(value -> value instanceof String
                        ? "'" + value + "'"
                        : value instanceof QueryBuilder ? "(" + value + ")" : value.toString())
                .collect(Collectors.joining(", "));

        if (this.query.toString().endsWith("(")) {
            appendCondition(condition, "IN (" + valuesString + ")");
        } else if (this.query.toString().contains("WHERE")) {
            this.query.append("AND ");
            appendCondition(condition, "IN (" + valuesString + ")");
        } else {
            this.query.append("WHERE ");
            appendCondition(condition, "IN (" + valuesString + ")");
        }

        this.query.deleteCharAt(this.query.lastIndexOf("["));
        this.query.deleteCharAt(this.query.lastIndexOf("]"));

        return this;
    }

    private void appendCondition(String condition, String valuesClause) {
        this.query.append(condition).append(" ").append(valuesClause).append(" ");
    }

    public QueryBuilder orWhere(String condition, String parameter, Object value) {
        if (this.query.toString().contains("WHERE")) {
            this.query.append("OR ");
        } else {
            this.query.append("WHERE ");
        }

        appendCondition(condition, parameter, value);

        return this;
    }

    private void appendCondition(String condition, String parameter, Object value) {
        this.query.append(condition)
                .append(" ")
                .append(parameter)
                .append(" ")
                .append(value instanceof String ? "'" + value + "'" :
                        value instanceof QueryBuilder ? "(" + value + ")" : value)
                .append(" ");
    }

    public QueryBuilder orWhere(Function<QueryBuilder, QueryBuilder> function) {
        if (this.query.toString().contains("WHERE")) {
            this.query.append("OR (").append(function.apply(this)).append(") ");
        } else {
            this.query.append("WHERE (").append(function.apply(this)).append(") ");
        }

        return this;
    }

    public QueryBuilder groupBy(String... columns) {
        if (this.query.toString().contains("GROUP BY"))
            throw new IllegalArgumentException("Group by already exists");
        this.query.append("GROUP BY ").append(String.join(", ", columns)).append(" ");
        return this;
    }

    public QueryBuilder orderBy(String column, String direction) {
        if (this.query.toString().contains("LIMIT") || this.query.toString().contains("OFFSET"))
            throw new IllegalArgumentException("Can't add order by after limit or offset");

        if (!direction.equalsIgnoreCase("ASC") && !direction.equalsIgnoreCase("DESC"))
            throw new IllegalArgumentException("Invalid direction");

        this.query.append("ORDER BY ").append(column).append(" ").append(direction).append(" ");
        return this;
    }

    public QueryBuilder limit(int limit) {
        if (this.query.toString().contains("LIMIT") || this.query.toString().contains("OFFSET"))
            throw new IllegalArgumentException("Limit already exists or offset exists");

        if (limit < 0)
            throw new IllegalArgumentException("Limit must be greater than 0");

        this.query.append("LIMIT ").append(limit).append(" ");
        return this;
    }

    public QueryBuilder having(String condition, Object value) {
        return having(condition, "=", value);
    }

    public QueryBuilder having(String condition, String parameter, Object value) {
        if (!parameters.contains(parameter.toLowerCase())) {
            throw new IllegalArgumentException("Invalid parameter");
        }

        if (this.query.toString().contains("ORDER BY")
                || this.query.toString().contains("LIMIT")
                || this.query.toString().contains("OFFSET")) {
            throw new IllegalArgumentException("Can't add HAVING after ORDER BY, LIMIT, or OFFSET");
        }

        if (this.query.toString().endsWith("(")) {
            appendCondition(condition, parameter, value);
        } else if (!this.query.toString().contains("HAVING")) {
            this.query.append("HAVING ");
            appendCondition(condition, parameter, value);
        } else {
            this.query.append("AND ");
            appendCondition(condition, parameter, value);
        }

        return this;
    }

    public QueryBuilder orHaving(String condition, String parameter, Object value) {
        if (this.query.toString().contains("ORDER BY")
                || this.query.toString().contains("LIMIT")
                || this.query.toString().contains("OFFSET")) {
            throw new IllegalArgumentException("Can't add HAVING after ORDER BY, LIMIT, or OFFSET");
        }

        if (this.query.toString().endsWith("(")) {
            appendCondition(condition, parameter, value);
        } else if (!this.query.toString().contains("HAVING")) {
            this.query.append("HAVING ");
            appendCondition(condition, parameter, value);
        } else {
            this.query.append("OR ");
            appendCondition(condition, parameter, value);
        }

        return this;
    }

    public QueryBuilder orHaving(Function<QueryBuilder, QueryBuilder> function) {
        if (this.query.toString().contains("ORDER BY")
                || this.query.toString().contains("LIMIT")
                || this.query.toString().contains("OFFSET")) {
            throw new IllegalArgumentException("Can't add HAVING after ORDER BY, LIMIT, or OFFSET");
        }

        if (this.query.toString().contains("HAVING")) {
            this.query.append("OR (").append(function.apply(this)).append(") ");
        } else {
            this.query.append("HAVING (").append(function.apply(this)).append(") ");
        }

        return this;
    }

    public QueryBuilder having(Function<QueryBuilder, QueryBuilder> function) {
        String condition = function.apply(new QueryBuilder()).toString();

        if (!condition.isEmpty()) {
            if (this.query.toString().contains("HAVING")) {
                this.query.append(" AND (").append(condition).append(") ");
            } else {
                this.query.append("HAVING (").append(condition).append(") ");
            }
        }

        return this;
    }

    public QueryBuilder offset(int offset) {
        if (this.query.toString().contains("OFFSET"))
            throw new IllegalArgumentException("Offset already exists");

        if (offset < 0)
            throw new IllegalArgumentException("Offset must be greater than 0");

        this.query.append("OFFSET ").append(offset).append(" ");
        return this;
    }

    public QueryBuilder when(boolean condition, Function<QueryBuilder, QueryBuilder> trueFunction) {
        return condition ? trueFunction.apply(this) : this;
    }

    public QueryBuilder when(boolean condition, Function<QueryBuilder, QueryBuilder> trueFunction, Function<QueryBuilder, QueryBuilder> falseFunction) {
        return condition ? trueFunction.apply(this) : falseFunction.apply(this);
    }

    public String toString() {
        return this.query.toString();
    }
}
