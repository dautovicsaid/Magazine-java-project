import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Magazine;

import com.project.magazines.enumeration.JoinType;
import com.project.magazines.enumeration.LogicalOperator;
import com.project.magazines.helper.Condition;
import com.project.magazines.helper.Join;

import com.project.magazines.connection.DatabaseConnection;

import java.util.List;

public class MagazineService {

    private final DatabaseConnection dbConnection;

    public MagazineService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<Magazine> getAll() {
        return getAll(null);
    }

    // TODO: Add date filter
    public List<Magazine> getAll(String search) {
        List<Condition> conditions = (search == null) ? null : List.of(
                new Condition(LogicalOperator.WHERE, "magazine.name", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "magazine.address", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "city.name", "LIKE", "%" + search + "%"),
                new Condition(LogicalOperator.OR, "country.name", "LIKE", "%" + search + "%")
        );

        return null;
    }






}
