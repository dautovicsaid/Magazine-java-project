package com.project.magazines.helper;

import com.project.magazines.enumeration.LogicalOperator;

public record Condition(LogicalOperator logicalOperator, String columnName, String comparator, Object value) {
}
