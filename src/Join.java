public class Join {
    private final String joinTableName;
    private final String sourceColumn;
    private final String targetColumn;

    public Join(String joinTableName, String sourceColumn, String targetColumn) {
        this.joinTableName = joinTableName;
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public String getSourceColumn() {
        return sourceColumn;
    }

    public String getTargetColumn() {
        return targetColumn;
    }
}
