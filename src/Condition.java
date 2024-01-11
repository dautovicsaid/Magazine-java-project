public class Condition {
    private final String columnName;
    private final String comparator;
    private final Object value;

    public Condition(String columnName, String comparator, Object value) {
        this.columnName = columnName;
        this.comparator = comparator;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getComparator() {
        return comparator;
    }

    public Object getValue() {
        return value;
    }
}
