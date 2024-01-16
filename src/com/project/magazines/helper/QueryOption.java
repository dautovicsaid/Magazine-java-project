package com.project.magazines.helper;

import com.project.magazines.enumeration.OrderByDirection;
import com.project.magazines.enumeration.QueryOptionType;


public class QueryOption {
    private QueryOptionType type;
    private String columnName;
    private OrderByDirection orderByDirection;
    private int limitCount;
    private int offsetCount;

    // ORDER BY
    public QueryOption(QueryOptionType type, String columnName, OrderByDirection orderByDirection) {
        this.type = type;
        this.columnName = columnName;
        this.orderByDirection = orderByDirection;
    }

    // GROUP BY
    public QueryOption(QueryOptionType type, String columnName) {
        this.type = type;
        this.columnName = columnName;
    }

    // Constructor for LIMIT
    public QueryOption(QueryOptionType type, int limitCount) {
        this.type = type;
        this.limitCount = limitCount;
    }

    // Constructor for OFFSET
    public QueryOption(int offsetCount, QueryOptionType type) {
        this.type = type;
        this.offsetCount = offsetCount;
    }

    public QueryOptionType getType() {
        return type;
    }

    public void setType(QueryOptionType type) {
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public OrderByDirection getOrderByDirection() {
        return orderByDirection;
    }

    public void setOrderByDirection(OrderByDirection orderByDirection) {
        this.orderByDirection = orderByDirection;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public int getOffsetCount() {
        return offsetCount;
    }

    public void setOffsetCount(int offsetCount) {
        this.offsetCount = offsetCount;
    }
}
