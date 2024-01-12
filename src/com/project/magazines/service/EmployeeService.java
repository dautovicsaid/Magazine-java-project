package com.project.magazines.service;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.enumeration.Employee;

import java.util.Map;

public class EmployeeService {

    private final DatabaseConnection dbConnection;

    private final CityService cityService;

    public EmployeeService(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.cityService = new CityService(dbConnection);
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

        Map<String, Object> data = new java.util.HashMap<>(Map.of(
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
            case EDITOR -> {
                data.put("employment_date", employee.getEmploymentDate());
            }
            case REGULAR -> {
                data.put("employment_date", employee.getEmploymentDate());
                data.put("professional_qualification_level", employee.getProfessionalQualificationLevel());
            }
            case PART_TIME -> {
                data.put("fee_per_article", employee.getFeePerArticle());
            }
        }

        return dbConnection.insert("employee", data);
    }

    private boolean checkIfEmployeeExists(Employee employee) {
        if (employee == null)
            return false;

        return dbConnection.findSingleIdByColumn("employee", "jmbg", employee.getJmbg()) != -1L;
    }
}
