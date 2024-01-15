package com.project.magazines.helper;

import com.project.magazines.enumeration.JoinType;

public record Join(JoinType joinType,String targetTableName, String joinTableName, String sourceColumn, String targetColumn) {

    public Join(JoinType joinType, String joinTableName, String sourceColumn, String targetColumn) {
        this(joinType, null, joinTableName, sourceColumn, targetColumn);
    }
}
