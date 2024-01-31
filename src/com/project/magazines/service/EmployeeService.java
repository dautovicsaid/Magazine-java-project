
package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.dto.EmployeeDto;
import com.project.magazines.dto.SimpleEmployeeDto;
import com.project.magazines.entity.Area;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.entity.Employee;
import com.project.magazines.enumeration.EmployeeType;
import com.project.magazines.helper.QueryBuilder;

import java.util.*;

public class EmployeeService {

    private final DatabaseConnection dbConnection;

    private final CityService cityService;
    private final AreaService areaService;

    public EmployeeService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.cityService = new CityService(dbConnection);
        this.areaService = new AreaService(dbConnection);
    }

    public List<SimpleEmployeeDto> getAll() {
        return getAll(null, null);
    }

    public List<SimpleEmployeeDto> getAll(EmployeeType type) {
        return getAll(null, type);
    }

    public List<SimpleEmployeeDto> getAll(String search) {
        return getAll(search, null);
    }


    // TODO: Add date filter
    public List<SimpleEmployeeDto> getAll(String search, EmployeeType type) {
        List<Map<String, Object>> employees = dbConnection.executeSelectQuery(
                QueryBuilder.query().select(
                                "employee.name",
                                "employee.last_name",
                                "employee.jmbg",
                                "employee.type",
                                "employee.employment_date",
                                "employee.professional_qualification_level",
                                "employee.fee_per_article",
                                "employee.id",
                                "city.name as city_name",
                                "country.name as country_name",
                                "GROUP_CONCAT(area.name) as areas")
                        .from("employee")
                        .join("city", "employee.city_id", "city.id")
                        .join("country", "city.country_id", "country.id")
                        .leftJoin("employee_area", "employee.id", "employee_area.employee_id")
                        .leftJoin("area", "employee_area.area_id", "area.id")
                        .when(type != null, query -> query.where("employee.type", "=", type.name()))
                        .groupBy("employee.id", "employee.name", "employee.last_name", "employee.jmbg", "employee.type", "employee.employment_date", "employee.professional_qualification_level", "employee.fee_per_article", "city.name", "country.name")
                        .when(search != null, query ->
                                query.having("CONCAT(employee.name, ' ', employee.last_name)", "LIKE", "%" + search + "%")
                                        .orHaving("employee.jmbg", "LIKE", "%" + search + "%")
                                        .orHaving("employee.employment_date", "LIKE", "%" + search + "%")
                                        .orHaving("employee.professional_qualification_level", "LIKE", "%" + search + "%")
                                        .orHaving("employee.fee_per_article", "LIKE", "%" + search + "%")
                                        .orHaving("city.name", "LIKE", "%" + search + "%")
                                        .orHaving("country.name", "LIKE", "%" + search + "%")
                        .orHaving("areas", "LIKE", "%" + search + "%"))
                        .toString());

        return employees.stream()
                .map(this::mapToSimpleEmployee)
                .toList();
    }

    private SimpleEmployeeDto mapToSimpleEmployee(Map<String, Object> employeeData) {
        return new SimpleEmployeeDto(
                (Long) employeeData.get("id"),
                employeeData.get("name") + " " + employeeData.get("last_name"),
                (String) employeeData.get("jmbg"),
                EmployeeType.valueOf((String) employeeData.get("type")),
                (String) employeeData.get("areas"),
                employeeData.get("city_name") + ", " + employeeData.get("country_name"),
                (Date) employeeData.get("employment_date"),
                (String) employeeData.get("professional_qualification_level"),
                (Integer) employeeData.get("fee_per_article")
        );

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
                new City((Long) employeeData.get("city_id"), (String) employeeData.get("city_name"),
                        new Country((Long) employeeData.get("country_id"),
                                (String) employeeData.get("country_name"))));
    }

    private EmployeeDto mapToEmployeeDto(Map<String, Object> employeeData) {
        return new EmployeeDto(
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
                (String) employeeData.get("city_name"),
                (String) employeeData.get("country_name"),
                (String) employeeData.get("areas")
        );

    }

    public boolean save(Employee employee) {
        if (employee == null) {
            System.out.println("Employee is null.");
            return false;
        }

        City city = cityService.getById(employee.getCity().getId());


        if (city == null) {
            System.out.println("City with id " + employee.getCity().getId() + " does not exist.");
            return false;
        }

        if (checkIfEmployeeExists(employee)) {
            System.out.println("Employee with JMBG " + employee.getJmbg() + " already exists.");
            return false;
        }

        Map<String, Object> data = prepareEmployeeData(employee, city);

        if (data.values().stream().anyMatch(Objects::isNull)) {
            System.out.println("Cannot create employee with null values.");
            return false;
        }

        if (employee.getId() == null)
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("employee", data)
                    .toString());
        else
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .update("employee", data)
                    .where("id", "=", employee.getId())
                    .toString());

        return true;
    }

    public boolean save(Employee employee, List<Long> areaIds) {
        if (employee == null) {
            System.out.println("Employee is null.");
            return false;
        }

        if (employee.getType() == null || employee.getType() != EmployeeType.EDITOR) {
            System.out.println("Employee type must be regular.");
            return false;
        }

        if (employee.getId() == null && (areaIds == null || areaIds.isEmpty())) {
            System.out.println("Employee must have at least one area.");
            return false;
        }

        Long existingAreasCount = dbConnection.executeCountQuery(
                QueryBuilder.query()
                        .count("id")
                        .from("area")
                        .whereIn("id", Collections.singletonList(areaIds))
                        .toString());

        if (existingAreasCount != areaIds.size()) {
            System.out.println("Some of the areas do not exist.");
            return false;
        }

        City city = cityService.getById(employee.getCity().getId());


        if (city == null) {
            System.out.println("City with id " + employee.getCity().getId() + " does not exist.");
            return false;
        }

        if (checkIfEmployeeExists(employee)) {
            System.out.println("Employee with JMBG " + employee.getJmbg() + " already exists.");
            return false;
        }

        Map<String, Object> data = prepareEmployeeData(employee, city);

        if (data.values().stream().anyMatch(Objects::isNull)) {
            System.out.println("Cannot create employee with null values.");
            return false;
        }

        if (employee.getId() == null) {
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("employee", data)
                    .toString());

            areaIds.forEach(areaId -> dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("employee_area", Map.of(
                            "employee_id", findId(employee.getJmbg()),
                            "area_id", areaId))
                    .toString()));

        } else {
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .update("employee", data)
                    .where("id", "=", employee.getId())
                    .toString());

            if (!areaIds.isEmpty()) {
                dbConnection.executeDeleteQuery(QueryBuilder.query()
                        .delete("employee_area")
                        .where("employee_id", "=", employee.getId())
                        .toString());

                areaIds.forEach(areaId -> dbConnection.executeSaveQuery(QueryBuilder.query()
                        .insert("employee_area", Map.of(
                                "employee_id", employee.getId(),
                                "area_id", areaId))
                        .toString()));
            }
        }

        return true;
    }

    public boolean save(Employee employee, Long areaId) {
        if (employee == null) {
            System.out.println("Employee is null.");
            return false;
        }

        if (employee.getType() == null || employee.getType() != EmployeeType.REGULAR) {
            System.out.println("Employee type must be regular.");
            return false;
        }

        Area area = areaService.getById(areaId);

        if (area == null) {
            System.out.println("Area with id " + areaId + " does not exist.");
            return false;
        }

        City city = cityService.getById(employee.getCity().getId());


        if (city == null) {
            System.out.println("City with id " + employee.getCity().getId() + " does not exist.");
            return false;
        }

        if (checkIfEmployeeExists(employee)) {
            System.out.println("Employee with JMBG " + employee.getJmbg() + " already exists.");
            return false;
        }

        Map<String, Object> data = prepareEmployeeData(employee, city);

        if (data.values().stream().anyMatch(Objects::isNull)) {
            System.out.println("Cannot create employee with null values.");
            return false;
        }

        if (employee.getId() == null) {
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("employee", data)
                    .toString());

            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("employee_area", Map.of(
                            "employee_id", findId(employee.getJmbg()),
                            "area_id", areaId))
                    .toString());
        } else {
            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .update("employee", data)
                    .where("id", "=", employee.getId())
                    .toString());

            dbConnection.executeDeleteQuery(QueryBuilder.query()
                    .delete("employee_area")
                    .where("employee_id", "=", employee.getId())
                    .toString());

            dbConnection.executeSaveQuery(QueryBuilder.query()
                    .insert("employee_area", Map.of(
                            "employee_id", employee.getId(),
                            "area_id", areaId))
                    .toString());
        }

        return true;
    }

    public Employee getById(Long id) {
        if (id == null)
            return null;

        Map<String, Object> result = getSingleEmployeeData(id);

        return result.isEmpty() ? null : mapToEmployee(result);
    }

    public EmployeeDto getDtoById(Long id) {
        if (id == null) {
            return null;
        }

        Map<String, Object> result = getSingleEmployeeData(id);

        return result.isEmpty() ? null : mapToEmployeeDto(result);
    }

    private Map<String, Object> getSingleEmployeeData(Long id) {
        return dbConnection.executeFindByIdQuery(
                QueryBuilder.query()
                        .select("employee.*",
                                "city.name as city_name",
                                "city.country_id as country_id",
                                "country.name as country_name",
                                "GROUP_CONCAT(area.name) as areas")
                        .from("employee")
                        .join("city", "employee.city_id", "city.id")
                        .join("country", "city.country_id", "country.id")
                        .leftJoin("employee_area", "employee.id", "employee_area.employee_id")
                        .leftJoin("area", "employee_area.area_id", "area.id")
                        .where("employee.id", "=", id)
                        .toString());
    }


    public boolean delete(Long id) {
        if (id == null) {
            System.out.println("Id is null!");
            return false;
        }

        if (dbConnection.executeExistsQuery(
                QueryBuilder.query()
                        .exists(
                                QueryBuilder.query()
                                        .select()
                                        .from("revision")
                                        .where("employee_id", "=", id)
                                , "result")
                        .toString())) {
            System.out.println("Cannot delete employee with id " + id + " because he has revisions.");
            return false;
        }

        dbConnection.executeDeleteQuery(
                QueryBuilder.query()
                        .delete("employee_area")
                        .where("employee_id", "=", id)
                        .toString());

        dbConnection.executeDeleteQuery(
                QueryBuilder.query()
                        .delete("employee")
                        .where("id", "=", id)
                        .toString());

        return true;
    }

    public Long findId(String jmbg) {
        if (jmbg == null)
            return -1L;

        return dbConnection.executeFindIdQuery(
                QueryBuilder.query()
                        .select("id")
                        .from("employee")
                        .where("jmbg", "=", jmbg)
                        .toString());
    }

    public List<SimpleEmployeeDto> employeesWithMoreThanAverageNumberOfAreas() {

        List<Map<String, Object>> employees = dbConnection.executeSelectQuery(
                QueryBuilder.query()
                        .select("employee.name",
                                "employee.last_name",
                                "employee.jmbg",
                                "employee.type",
                                "employee.employment_date",
                                "employee.professional_qualification_level",
                                "employee.fee_per_article",
                                "employee.id",
                                "city.name as city_name",
                                "country.name as country_name",
                                "GROUP_CONCAT(area.name) as areas")
                        .from("employee")
                        .join("city", "employee.city_id", "city.id")
                        .join("country", "city.country_id", "country.id")
                        .join("employee_area", "employee.id", "employee_area.employee_id")
                        .join("area", "employee_area.area_id", "area.id")
                        .groupBy("employee.id", "employee.name", "employee.last_name", "employee.jmbg", "employee.type", "employee.employment_date", "employee.professional_qualification_level", "employee.fee_per_article", "city.name", "country.name")
                        .having("COUNT(employee_area.area_id)", ">=", QueryBuilder.query()
                                .select("AVG(area_count)")
                                .from(QueryBuilder.query()
                                        .select("COUNT(employee_area.area_id) as area_count")
                                        .from("employee_area")
                                        .groupBy("employee_area.employee_id"), "subquery"))
                        .toString());

        return employees.stream()
                .map(this::mapToSimpleEmployee)
                .toList();
    }

    private Map<String, Object> prepareEmployeeData(Employee employee, City city) {
        Map<String, Object> data = new HashMap<>(Map.of(
                "name", employee.getName(),
                "last_name", employee.getLastName(),
                "jmbg", employee.getJmbg(),
                "address", employee.getAddress(),
                "phone_number", employee.getPhoneNumber(),
                "birth_date", employee.getBirthDate().toString(),
                "type", employee.getType().name(),
                "city_id", city.getId()
        ));

        switch (employee.getType()) {
            case EDITOR -> data.put("employment_date", employee.getEmploymentDate().toString());
            case REGULAR -> {
                data.put("employment_date", employee.getEmploymentDate().toString());
                data.put("professional_qualification_level", employee.getProfessionalQualificationLevel());
            }
            case PART_TIME -> data.put("fee_per_article", employee.getFeePerArticle());
        }

        return data;
    }

    private boolean checkIfEmployeeExists(Employee employee) {
        if (employee == null)
            return false;

        return dbConnection.executeExistsQuery(QueryBuilder.query()
                .exists(
                        QueryBuilder.query()
                                .select()
                                .from("employee")
                                .where("jmbg", "=", employee.getJmbg())
                                .when(employee.getId() != null, query -> query.where("id", "<>", employee.getId()))
                        , "result")
                .toString());
    }
}

