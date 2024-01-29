package com.project.magazines.helper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class QueryBuilder {

    private StringBuilder query;


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

    public QueryBuilder update(String table, Map<String, Object> values) {
        if (values.isEmpty())
            throw new IllegalArgumentException("Values can't be empty");

        if (!this.query.toString().isEmpty())
            throw new IllegalArgumentException("Can't add update to existing query");


        this.query.append("UPDATE ").append(table).append(" SET ");
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            this.query.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue() instanceof String ? "'" + entry.getValue() + "'" : entry.getValue())
                    .append(", ");
        }

        this.query.deleteCharAt(this.query.length() - 2);
        return this;
    }

    public QueryBuilder insert(String table, Map<String, Object> values) {
        if (values.isEmpty())
            throw new IllegalArgumentException("Values can't be empty");

        if (!this.query.toString().isEmpty())
            throw new IllegalArgumentException("Can't add insert to existing query");

        this.query.append("INSERT INTO ").append(table).append(" (");

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            this.query.append(entry.getKey()).append(", ");
        }

        this.query.deleteCharAt(this.query.length() - 2);
        this.query.append(") VALUES (");

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue() instanceof String)
                this.query.append("'").append(entry.getValue()).append("', ");
            else
                this.query.append(entry.getValue()).append(", ");
        }

        this.query.deleteCharAt(this.query.length() - 2);
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
        if (!parameters.contains(parameter.toLowerCase()))
            throw new IllegalArgumentException("Invalid parameter");

        if (this.query.toString().contains("WHERE")) {
            this.query.append("AND ")
                    .append(condition)
                    .append(" ")
                    .append(parameter)
                    .append(" ")
                    .append(value instanceof String ? "'" + value + "'" : value)
                    .append(" ");
        } else {
            this.query.append("WHERE ")
                    .append(condition)
                    .append(" ")
                    .append(parameter)
                    .append(" ")
                    .append(value instanceof String ? "'" + value + "'" : value)
                    .append(" ");
        }

        return this;
    }

    public QueryBuilder where(Function<QueryBuilder, QueryBuilder> function) {
        String condition = function.apply(new QueryBuilder()).toString();

        if (!condition.isEmpty()) {
            if (this.query.toString().contains("WHERE")) {
                this.query.append(" AND (").append(condition).append(") ");
            } else {
                this.query.append("WHERE (").append(condition).append(") ");
            }
        }

        return this;
    }

    public QueryBuilder orWhere(String condition, String parameter, Object value) {
        if (this.query.toString().contains("WHERE")) {
            this.query.append("OR ")
                    .append(condition)
                    .append(" ")
                    .append(parameter)
                    .append(" ")
                    .append(value instanceof String ? "'" + value + "'" : value)
                    .append(" ");
        } else {
            this.query.append("WHERE ")
                    .append(condition)
                    .append(" ")
                    .append(parameter)
                    .append(" ")
                    .append(value instanceof String ? "'" + value + "'" : value)
                    .append(" ");
        }

        return this;
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
        if (this.query.toString().contains("LIMIT"))
            throw new IllegalArgumentException("Limit already exists");

        if (limit < 0)
            throw new IllegalArgumentException("Limit must be greater than 0");

        this.query.append("LIMIT ").append(limit).append(" ");
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
        String query = this.query.toString();
        this.query = new StringBuilder();
        return query;
    }


}
