package com.project.magazines.helper;

import java.util.List;
import java.util.function.Function;

public class QueryBuilder {

    private final StringBuilder query;

    private final List<String> parameters = List.of("=", "<", ">", "<=", ">=","<>","like", "not like");

    public QueryBuilder() {
        this.query = new StringBuilder();
    }

    public static QueryBuilder query() {
        return new QueryBuilder();
    }

    public QueryBuilder select(String... columns) {
        if (columns.length == 0) {
            this.query.append("SELECT * ");
        } else {
            this.query.append("SELECT ").append(String.join(", ", columns)).append(" ");
        }
        return this;
    }

    public QueryBuilder from(String table) {
        this.query.append("FROM ").append(table).append(" ");
        return this;
    }

    public QueryBuilder join(String table, String condition) {
        this.query.append("INNER JOIN ").append(table).append(" ON ").append(condition).append(" ");
        return this;
    }

    public QueryBuilder leftJoin(String table, String condition) {
        this.query.append("LEFT JOIN ").append(table).append(" ON ").append(condition).append(" ");
        return this;
    }

    public QueryBuilder rightJoin(String table, String condition) {
        this.query.append("RIGHT JOIN ").append(table).append(" ON ").append(condition).append(" ");
        return this;
    }

    public QueryBuilder fullJoin(String table, String condition) {
        this.query.append("FULL JOIN ").append(table).append(" ON ").append(condition).append(" ");
        return this;
    }

    public QueryBuilder where(String condition, String value) {
        return where(condition, " = ", value);
    }

    public QueryBuilder where(String condition, String parameter, Object value) {
        if (!parameters.contains(parameter))
            throw new IllegalArgumentException("Invalid parameter");

        if (this.query.toString().contains("WHERE")) {
            this.query.append("AND ")
                    .append(condition)
                    .append(parameter)
                    .append(value)
                    .append(" ");
        } else {
            this.query.append("WHERE ")
                    .append(condition)
                    .append(parameter)
                    .append(value)
                    .append(" ");
        }

        return this;
    }

    public QueryBuilder where(Function<QueryBuilder, QueryBuilder> function) {
        if (this.query.toString().contains("WHERE")) {
            this.query.append("AND (").append(function.apply(this)).append(") ");
        } else {
            this.query.append("WHERE (").append(function.apply(this)).append(") ");
        }

        return this;
    }

    public QueryBuilder orWhere(String condition, String parameter,  String value) {
        if (this.query.toString().contains("WHERE")) {
            this.query.append("OR ")
                    .append(condition)
                    .append(" = ")
                    .append(value)
                    .append(" ");
        } else {
            this.query.append("WHERE ")
                    .append(condition)
                    .append(" ")
                    .append(value)
                    .append(" ");
        }

        return this;
    }
}
