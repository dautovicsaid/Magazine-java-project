package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.entity.Employee;
import com.project.magazines.enumeration.EmployeeType;
import com.project.magazines.enumeration.JoinType;
import com.project.magazines.enumeration.LogicalOperator;
import com.project.magazines.helper.Condition;
import com.project.magazines.helper.Join;

import java.util.*;

public class EmployeeService {

    private final DatabaseConnection dbConnection;

    private final CityService cityService;

    public EmployeeService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.cityService = new CityService(dbConnection);
    }

    public List<Employee> getAll() {
        return getAll(null, null);
    }

    public List<Employee> getAll(EmployeeType type) {
        return getAll(null, type);
    }

    public List<Employee> getAll(String search) {
        return getAll(search, null);
    }


    // TODO: Add date filter
    public List<Employee> getAll(String search, EmployeeType type) {
        List<Map<String, Object>> employees = dbConnection.select("employee",
                List.of("employee.*", "city.name as city_name",
                        "country.name as country_name", "country.id as country_id", "GROUP_CONCAT(area.name) as areas"),
                (search == null && type == null) ? null : prepareSearchFilterConditions(search, type),
                List.of(new Join(JoinType.INNER, "city", "city_id", "id"),
                        new Join(JoinType.INNER,"city", "country", "country_id", "id"),
                        new Join(JoinType.LEFT, "employee_area", "employee_id", "id"),
                        new Join(JoinType.LEFT, "employee_area", "area", "area_id", "id"))
                );

        return employees.stream()
                .map(this::mapToEmployee)
                .toList();
    }

    private List<Condition> prepareSearchFilterConditions(String search, EmployeeType type) {
        if (search == null && type != null)
            return List.of(getFilterByTypeCondition(type, LogicalOperator.WHERE));

        List<Condition> conditions = new ArrayList<>(List.of(
                new Condition(LogicalOperator.WHERE, "employee.name", "LIKE", "%" + search + "%")
        ));

        List.of("employee.last_name", "employee.jmbg", "employee.address", "employee.phone_number")
                .forEach(field -> addSearchCondition(conditions, search, field));


        if (type != null) {
            conditions.add(getFilterByTypeCondition(type, LogicalOperator.OR));

            switch (type) {
                case EDITOR -> addSearchCondition(conditions, search, "employee.employment_date");
                case REGULAR -> {
                    addSearchCondition(conditions, search, "employee.employment_date");
                    addSearchCondition(conditions, search, "employee.professional_qualification_level");
                }
                case PART_TIME -> addSearchCondition(conditions, search, "employee.fee_per_article");
            }
        }

        return conditions;
    }

    private Condition getFilterByTypeCondition(EmployeeType type, LogicalOperator operator) {
        return new Condition(operator, "employee.type", "=", type.name());
    }

    private void addSearchCondition(List<Condition> conditions, String search, String field) {
        conditions.add(new Condition(LogicalOperator.OR, field, "LIKE", "%" + search + "%"));
    }

    private Employee mapToEmployee(Map<String, Object> employeeData) {
        return new Employee(
                (Long) employeeData.get("id"),
                (String) employeeData.get("name"),
                (String) employeeData.get("last_name"),
                (String) employeeData.get("jmbg"),
                (String) employeeData.get("address"),
                (String) employeeData.get("phone_number"),
                (java.sql.Date) employeeData.get("birth_date"),
                (java.sql.Date) employeeData.get("employment_date"),
                EmployeeType.valueOf((String) employeeData.get("type")),
                (String) employeeData.get("professional_qualification_level"),
                (Integer) employeeData.get("fee_per_article"),
                new City(
                        (Long) employeeData.get("city_id"),
                        (String) employeeData.get("city_name"),
                        new Country(
                                (Long) employeeData.get("country_id"),
                                (String) employeeData.get("country_name"))));

    }

    public boolean create(Employee employee) {
        if (employee == null) {
            System.out.println("Employee is null.");
            return false;
        }

        Long cityId = cityService.findId(employee.getCity());

        if (cityId == -1L) {
            System.out.println("City with name " + employee.getCity().getName() + " doesn't exist.");
            return false;
        }

        if (checkIfEmployeeExists(employee)) {
            System.out.println("Employee with JMBG " + employee.getJmbg() + " already exists.");
            return false;
        }

        Map<String, Object> data = prepareEmployeeData(employee, cityId);

        if (data.values().stream().anyMatch(Objects::isNull)) {
            System.out.println("Cannot create employee with null values.");
            return false;
        }

        return dbConnection.insert("employee", data);
    }

    public boolean update(Employee employee, Long id) {
        if (employee == null || id == null) {
            System.out.println("Cannot use null values as parameters.");
            return false;
        }

        Long cityId = cityService.findId(employee.getCity());

        if (cityId == -1L) {
            System.out.println("City with name " + employee.getCity().getName() + " doesn't exist.");
            return false;
        }

        if (checkIfEmployeeExists(employee, id)) {
            System.out.println("Employee with JMBG " + employee.getJmbg() + " already exists.");
            return false;
        }

        Map<String, Object> data = prepareEmployeeData(employee, cityId);

        if (data.values().stream().anyMatch(Objects::isNull)) {
            System.out.println("Cannot create employee with null values.");
            return false;
        }

        return dbConnection.update("employee", data, id);
    }

    public boolean delete(Long id) {
        if (id == null)
            return false;

        return dbConnection.delete("employee", id);
    }

    public Long findId(String jmbg) {
        if (jmbg == null)
            return -1L;

        return dbConnection.findSingleIdByColumn("employee", "jmbg", jmbg);
    }

    private Map<String, Object> prepareEmployeeData(Employee employee, Long cityId) {
        Map<String, Object> data = new HashMap<>(Map.of(
                "name", employee.getName(),
                "last_name", employee.getLastName(),
                "jmbg", employee.getJmbg(),
                "address", employee.getAddress(),
                "phone_number", employee.getPhoneNumber(),
                "birth_date", employee.getBirthDate(),
                "type", employee.getType().name(),
                "city_id", cityId
        ));

        switch (employee.getType()) {
            case EDITOR -> data.put("employment_date", employee.getEmploymentDate());
            case REGULAR -> {
                data.put("employment_date", employee.getEmploymentDate());
                data.put("professional_qualification_level", employee.getProfessionalQualificationLevel());
            }
            case PART_TIME -> data.put("fee_per_article", employee.getFeePerArticle());
        }

        return data;
    }

    private boolean checkIfEmployeeExists(Employee employee) {
        if (employee == null)
            return false;

        return dbConnection.findSingleIdByColumn("employee", "jmbg", employee.getJmbg()) != -1L;
    }

    private boolean checkIfEmployeeExists(Employee employee, Long id) {
        if (employee == null)
            return false;

        Long existingEmployeeId = dbConnection.findSingleIdByColumn("employee", "jmbg", employee.getJmbg());

        return existingEmployeeId != -1L && !existingEmployeeId.equals(id);
    }
}
