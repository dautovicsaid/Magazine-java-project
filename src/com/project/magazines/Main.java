package com.project.magazines;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.dto.EmployeeDto;
import com.project.magazines.dto.SimpleEmployeeDto;
import com.project.magazines.entity.Area;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.entity.Employee;
import com.project.magazines.enumeration.EmployeeType;
import com.project.magazines.service.AreaService;
import com.project.magazines.service.CountryService;
import com.project.magazines.service.CityService;
import com.project.magazines.service.EmployeeService;


import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DatabaseConnection dbconn = new DatabaseConnection("root", "password", "127.0.0.1", "3306", "magazines");
    private static final CountryService countryService = new CountryService(dbconn);
    private static final AreaService areaService = new AreaService(dbconn);
    private static final CityService cityService = new CityService(dbconn);
    private static final EmployeeService employeeService = new EmployeeService(dbconn);

    public static void main(String[] args) {
        generalMenu();
    }

    private static void generalMenu() {
        System.out.println("""
                +-----------------------------+
                | Please select the table:    |
                +-----------------------------+
                | 1. Country                  |
                | 2. City                     |
                | 3. Area                     |
                | 4. Employee                 |
                +-----------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> countryMenu();
            case 2 -> cityMenu();
            case 3 -> areaMenu();
            case 4 -> employeeMenu();
            default -> System.out.println("Invalid input!");
        }
    }

    private static void employeeMenu() {
        System.out.println("""
                +--------------------------+
                | Please select operation: |
                +--------------------------+
                | 1. Create                |
                | 2. Update                |
                | 3. Delete                |
                | 4. Read one              |
                | 5. Read all              |
                | 6. Employees with more   |
                | areas than average       |
                | 7. Go back               |
                +--------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> createEmployee();
            case 2 -> updateEmployee();
            case 3 -> deleteEmployee();
            case 4 -> readEmployee();
            case 5 -> readAllEmployee();
            case 6 -> employeesWithMoreThanAverageNumberOfAreas();
            case 7 -> generalMenu();
            default -> System.out.println("Invalid input!");
        }
    }

    private static void employeesWithMoreThanAverageNumberOfAreas() {
        List<SimpleEmployeeDto> employees = employeeService.employeesWithMoreThanAverageNumberOfAreas();
        displaySimpleEmployees(employees);
        employeeMenu();
    }

    private static void readAllEmployee() {
        System.out.println("Do you want to insert search term? (y/n)");
        scanner.nextLine();

        String search;
        String decision = scanner.nextLine();

        if (decision.equals("y") || decision.equals("Y")) {
            System.out.println("Enter the search term:");
            search = scanner.nextLine();
        } else if (decision.equals("n") || decision.equals("N")) {
            search = null;
        } else {
            System.out.println("Invalid input!");
            readAllEmployee();
            return;
        }

        System.out.println("Do you want to insert employee type? (y/n)");

        decision = scanner.nextLine();

        if (decision.equals("y") || decision.equals("Y")) {
            System.out.println("""
                    +--------------------------+
                    | Please select operation: |
                    +--------------------------+
                    | 1. Editor                |
                    | 2. Regular               |
                    | 3. Part-time             |
                    +--------------------------+
                    """);

            int result = scanner.nextInt();
            switch (result) {
                case 1 -> displaySimpleEmployees(employeeService.getAll(search, EmployeeType.EDITOR));
                case 2 -> displaySimpleEmployees(employeeService.getAll(search, EmployeeType.REGULAR));
                case 3 -> displaySimpleEmployees(employeeService.getAll(search, EmployeeType.PART_TIME));
                default -> System.out.println("Invalid input!");
            }
        } else if (decision.equals("n") || decision.equals("N")) {
            displaySimpleEmployees(employeeService.getAll(search));
        } else {
            System.out.println("Invalid input!");
            readAllEmployee();
        }

        employeeMenu();
    }

    private static void readEmployee() {

        displaySimpleEmployees(employeeService.getAll());
        System.out.println("Enter the id of the employee you want to read:");
        Long id = scanner.nextLong();
        EmployeeDto employee = employeeService.getDtoById(id);
        if (employee == null) {
            System.out.println("Employee with id " + id + " does not exist!");
            employeeMenu();
            return;
        }

        displayEmployeeDetails(employee);
        employeeMenu();
    }

    private static void deleteEmployee() {

    }

    private static void updateEmployee() {
        displaySimpleEmployees(employeeService.getAll());
        System.out.println("Enter the id of the employee you want to update:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        Employee employee = employeeService.getById(id);

        if (employee == null) {
            System.out.println("Employee with id " + id + " does not exist!");
            employeeMenu();
            return;
        }

        scanner.nextLine();
        updateField("name", () -> {
            System.out.println("Enter the new name of the employee:");
            employee.setName(scanner.nextLine());
        });

        updateField("last name", () -> {
            System.out.println("Enter the new last name of the employee:");
            employee.setLastName(scanner.nextLine());
        });

        updateField("address", () -> {
            System.out.println("Enter the new address of the employee:");
            employee.setAddress(scanner.nextLine());
        });

        updateField("phone number", () -> {
            System.out.println("Enter the new phone number of the employee:");
            employee.setPhoneNumber(scanner.nextLine());
        });

        updateField("birth date", () -> {
            System.out.println("Enter the new birth date of the employee (yyyy-mm-dd):");
            employee.setBirthDate(Date.valueOf(scanner.nextLine()));
        });

        updateField("city", () -> {
            displayCities(cityService.getAll());
            System.out.println("Enter the id of the city:");
            Long cityId = scanner.nextLong();
            City city = cityService.getById(cityId);
            if (city == null) {
                System.out.println("City with id " + cityId + " does not exist!");
                employeeMenu();
                return;
            }
            employee.setCity(city);
        });

        switch (employee.getType()) {
            case EDITOR -> {
                updateField("employment date", () -> {
                    scanner.nextLine();
                    System.out.println("Enter the new employment date of the employee (yyyy-mm-dd):");
                    employee.setEmploymentDate(Date.valueOf(scanner.nextLine()));
                });

                updateField("areas", () -> {
                    System.out.println("All areas from the employee will be deleted! Do you want to continue? (y/n)");
                    handleUpdateAreas(employee);
                });
            }
            case PART_TIME -> updateField("fee per article", () -> {
                System.out.println("Enter the new fee per article of the employee (in cents):");
                employee.setFeePerArticle(scanner.nextInt());

                if (employeeService.save(employee))
                    System.out.println("Employee successfully updated!");
            });
            case REGULAR -> {
                updateField("employment date", () -> {
                    System.out.println("Enter the new employment date of the employee (yyyy-mm-dd):");
                    employee.setEmploymentDate(Date.valueOf(scanner.nextLine()));
                });

                updateField("professional qualification level", () -> {
                    System.out.println("Enter the new professional qualification level of the employee:");
                    employee.setProfessionalQualificationLevel(scanner.nextLine());
                });

                updateField("area", () -> {
                    displayAreas(areaService.getAll());
                    System.out.println("Enter the id of the area:");
                    Long areaId = scanner.nextLong();
                    Area area = areaService.getById(areaId);
                    if (area == null) {
                        System.out.println("Area with id " + areaId + " does not exist!");
                        employeeMenu();
                        return;
                    }

                    if(employeeService.save(employee, areaId))
                        System.out.println("Employee successfully updated!");
                });
            }
        }

        employeeMenu();
    }

    private static void updateField(String fieldName, Runnable updateAction) {
        System.out.println("Do you want to update " + fieldName + "? (y/n)");
        switch (scanner.nextLine()) {
            case "y", "Y" -> updateAction.run();
            case "n", "N" -> {  }
            default -> {
                System.out.println("Invalid input!");
                updateEmployee();
            }
        }
    }

    private static void handleUpdateAreas(Employee employee) {
        switch (scanner.nextLine()) {
            case "y", "Y" -> {
                displayAreas(areaService.getAll());
                System.out.println("Enter the ids of the area separated by comma:");
                List<Long> areaIds = Arrays.stream(scanner.nextLine().split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                employeeService.save(employee, areaIds);
            }
            case "n", "N" -> {  }
            default -> {
                System.out.println("Invalid input!");
                updateEmployee();
            }
        }

        if (employeeService.save(employee))
            System.out.println("Employee successfully updated!");
    }

    private static void createEmployee() {
        System.out.println("Enter the JMBG of the employee:");
        scanner.nextLine();
        String jmbg = scanner.nextLine();
        if (employeeService.findId(jmbg) != -1) {
            System.out.println("Employee with JMBG " + jmbg + " already exists!");
            employeeMenu();
            return;
        }

        System.out.println("""
                +--------------------------+
                | Please select operation: |
                +--------------------------+
                | 1. Editor                |
                | 2. Regular               |
                | 3. Part-time             |
                +--------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> createEditor(jmbg);
            case 2 -> createRegular(jmbg);
            case 3 -> createPartTime(jmbg);
            default -> System.out.println("Invalid input!");
        }
    }

    private static void createPartTime(String jmbg) {
        System.out.println("Enter the name of the employee:");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.println("Enter the last name of the employee:");
        String lastName = scanner.nextLine();
        System.out.println("Enter the address of the employee:");
        String address = scanner.nextLine();
        System.out.println("Enter the phone number of the employee:");
        String phoneNumber = scanner.nextLine();
        System.out.println("Enter the birth date of the employee (yyyy-mm-dd):");
        Date birthDate = Date.valueOf(scanner.nextLine());
        System.out.println("Enter the fee per article of the employee (in cents): ");
        Integer feePerArticle = scanner.nextInt();
        displayCities(cityService.getAll());
        System.out.println("Enter the id of the city:");
        Long cityId = scanner.nextLong();
        City city = cityService.getById(cityId);
        if (city == null) {
            System.out.println("City with id " + cityId + " does not exist!");
            employeeMenu();
            return;
        }

        if (employeeService.save(new Employee(
                name,
                lastName,
                jmbg,
                address,
                phoneNumber,
                birthDate,
                EmployeeType.PART_TIME,
                feePerArticle,
                city)))
            System.out.println("Employee successfully created!");

        employeeMenu();
    }

    private static void createRegular(String jmbg) {
        System.out.println("Enter the name of the employee:");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.println("Enter the last name of the employee:");
        String lastName = scanner.nextLine();
        System.out.println("Enter the address of the employee:");
        String address = scanner.nextLine();
        System.out.println("Enter the phone number of the employee:");
        String phoneNumber = scanner.nextLine();
        System.out.println("Enter the birth date of the employee (yyyy-mm-dd):");
        Date birthDate = Date.valueOf(scanner.nextLine());
        System.out.println("Enter the employment date of the employee (yyyy-mm-dd):");
        Date employmentDate = Date.valueOf(scanner.nextLine());
        System.out.println("Enter the professional qualification level of the employee:");
        String pql = scanner.nextLine();

        displayCities(cityService.getAll());
        System.out.println("Enter the id of the city:");
        Long cityId = scanner.nextLong();
        City city = cityService.getById(cityId);
        if (city == null) {
            System.out.println("City with id " + cityId + " does not exist!");
            employeeMenu();
            return;
        }

        displayAreas(areaService.getAll());
        System.out.println("Enter the id of the area:");
        Long areaId = scanner.nextLong();

        if (employeeService.save(new Employee(
                name,
                lastName,
                jmbg,
                address,
                phoneNumber,
                birthDate,
                employmentDate,
                EmployeeType.REGULAR,
                pql,
                city), areaId))
            System.out.println("Employee successfully created!");

        employeeMenu();
    }

    private static void createEditor(String jmbg) {
        System.out.println("Enter the name of the employee:");
        scanner.nextLine();
        String name = scanner.nextLine();
        System.out.println("Enter the last name of the employee:");
        String lastName = scanner.nextLine();
        System.out.println("Enter the address of the employee:");
        String address = scanner.nextLine();
        System.out.println("Enter the phone number of the employee:");
        String phoneNumber = scanner.nextLine();
        System.out.println("Enter the birth date of the employee (yyyy-mm-dd):");
        Date birthDate = Date.valueOf(scanner.nextLine());
        System.out.println("Enter the employment date of the employee (yyyy-mm-dd):");
        Date employmentDate = Date.valueOf(scanner.nextLine());


        displayCities(cityService.getAll());
        System.out.println("Enter the id of the city:");
        Long cityId = scanner.nextLong();
        City city = cityService.getById(cityId);
        if (city == null) {
            System.out.println("City with id " + cityId + " does not exist!");
            employeeMenu();
            return;
        }

        displayAreas(areaService.getAll());
        System.out.println("Enter the ids of the area separated by comma:");
        scanner.nextLine();
        List<Long> areaIds = Stream.of(scanner.nextLine().split(",")).map(Long::parseLong).toList();

        if (employeeService.save(new Employee(
                name,
                lastName,
                jmbg,
                address,
                phoneNumber,
                birthDate,
                employmentDate,
                EmployeeType.EDITOR,
                city), areaIds))
            System.out.println("Employee successfully created!");

        employeeMenu();
    }

    private static void areaMenu() {
        System.out.println("""
                +--------------------------+
                | Please select operation: |
                +--------------------------+
                | 1. Create                |
                | 2. Update                |
                | 3. Delete                |
                | 4. Read one              |
                | 5. Read all              |
                | 6. Go back               |
                +--------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> createArea();
            case 2 -> updateArea();
            case 3 -> deleteArea();
            case 4 -> readArea();
            case 5 -> readAllArea();
            case 6 -> generalMenu();
            default -> System.out.println("Invalid input!");
        }
    }

    private static void readAllArea() {
        System.out.println("Do you want to search for a specific area? (y/n)");
        scanner.nextLine();
        switch (scanner.nextLine()) {
            case "y", "Y" -> {
                System.out.println("Enter the search term:");
                String search = scanner.nextLine();
                displayAreas(areaService.getAll(search));
            }
            case "n", "N" -> displayAreas(areaService.getAll());
            default -> System.out.println("Invalid input!");
        }

        areaMenu();
    }

    private static void readArea() {
        System.out.println("Enter the id of the area you want to read:");
        Long id = scanner.nextLong();
        Area area = areaService.getById(id);
        if (area == null) {
            System.out.println("Area with id " + id + " does not exist!");
            areaMenu();
            return;
        }
        displayAreas(List.of(area));
        areaMenu();
    }

    private static void deleteArea() {
        displayAreas(areaService.getAll());
        System.out.println("Enter the id of the area you want to delete:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        Area area = areaService.getById(id);
        if (area == null) {
            System.out.println("Area with id " + id + " does not exist!");
            areaMenu();
            return;
        }

        if (areaService.delete(id))
            System.out.println("Area successfully deleted!");

        areaMenu();
    }

    private static void updateArea() {
        displayAreas(areaService.getAll());
        System.out.println("Enter the id of the area you want to update:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        scanner.nextLine();
        Area area = areaService.getById(id);
        if (area == null) {
            System.out.println("Area with id " + id + " does not exist!");
            areaMenu();
            return;
        }
        System.out.println("Enter the new name of the area:");
        String newName = scanner.nextLine();
        area.setName(newName);
        if (areaService.save(area))
            System.out.println("Area successfully updated!");
        areaMenu();
    }

    private static void createArea() {
        System.out.println("Enter the name of the area you want to create:");
        scanner.nextLine();
        String name = scanner.nextLine();
        if (areaService.save(new Area(name)))
            System.out.println("Area successfully created!");
        areaMenu();
    }

    private static void cityMenu() {
        System.out.println("""
                +--------------------------+
                | Please select operation: |
                +--------------------------+
                | 1. Create                |
                | 2. Update                |
                | 3. Delete                |
                | 4. Read one              |
                | 5. Read all              |
                | 6. Go back               |
                +--------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> createCity();
            case 2 -> updateCity();
            case 3 -> deleteCity();
            case 4 -> readCity();
            case 5 -> readAllCity();
            case 6 -> generalMenu();
            default -> System.out.println("Invalid input!");
        }
    }

    private static void readAllCity() {
        scanner.nextLine();
        System.out.println("Do you want to enter a search term? (y/n)");
        switch (scanner.nextLine()) {
            case "y", "Y" -> {
                System.out.println("Enter the search term:");
                String search = scanner.nextLine();
                displayCities(cityService.getAll(search));
            }
            case "n", "N" -> displayCities(cityService.getAll());
            default -> {
                System.out.println("Invalid input!");
                readAllCity();
                return;
            }
        }
        cityMenu();
    }

    private static void readCity() {
        System.out.println("Enter the id of the city you want to read:");
        Long id = scanner.nextLong();
        City city = cityService.getById(id);
        if (city == null) {
            System.out.println("City with id " + id + " does not exist!");
            cityMenu();
            return;
        }
        displayCities(List.of(city));
        cityMenu();
    }

    private static void deleteCity() {
        displayCities(cityService.getAll());
        System.out.println("Enter the id of the city you want to delete:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        City city = cityService.getById(id);
        if (city == null) {
            System.out.println("City with id " + id + " does not exist!");
            cityMenu();
            return;
        }

        if (cityService.delete(id))
            System.out.println("City successfully deleted!");

        cityMenu();
    }

    private static void updateCity() {
        displayCities(cityService.getAll());
        System.out.println("Enter the id of the city you want to update:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        City city = cityService.getById(id);
        if (city == null) {
            System.out.println("City with id " + id + " does not exist!");
            cityMenu();
            return;
        }
        System.out.println("Enter the new name of the city:");
        scanner.nextLine();
        String newName = scanner.nextLine();

        city.setName(newName);
        System.out.println("Do you want to change the country? (y/n)");
        switch (scanner.nextLine()) {
            case "y", "Y" -> updateCityCountry(city);
            case "n", "N" -> {
                if (cityService.save(city))
                    System.out.println("City successfully updated!");
            }
            default -> {
                System.out.println("Invalid input!");
                updateCity();
                return;
            }
        }
        cityMenu();
    }

    private static void updateCityCountry(City city) {
        displayCountries(countryService.getAll());
        System.out.println("Enter the id of the country:");
        Long countryId = scanner.nextLong();
        Country country = countryService.getById(countryId);

        if (country == null) {
            System.out.println("Country with id " + countryId + " does not exist!");
            cityMenu();
            return;
        }

        city.setCountry(country);
        if (cityService.save(city))
            System.out.println("City successfully updated!");
    }

    private static void createCity() {
        System.out.println("Enter the name of the city you want to create:");
        scanner.nextLine();
        String name = scanner.nextLine();
        displayCountries(countryService.getAll());
        System.out.println("Enter the id of the country:");
        Long countryId = scanner.nextLong();
        Country country = countryService.getById(countryId);

        if (country == null) {
            System.out.println("Country with id " + countryId + " does not exist!");
            cityMenu();
            return;
        }

        if (cityService.save(new City(name, country)))
            System.out.println("City successfully created!");

        cityMenu();
    }

    private static void countryMenu() {
        System.out.println("""
                +--------------------------+
                | Please select operation: |
                +--------------------------+
                | 1. Create                |
                | 2. Update                |
                | 3. Delete                |
                | 4. Read one              |
                | 5. Read all              |
                | 6. Get country with      |
                | highest number of cities |
                | 7. Go back               |
                +--------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> createCountry();
            case 2 -> updateCountry();
            case 3 -> deleteCountry();
            case 4 -> readCountry();
            case 5 -> readAllCountry();
            case 6 -> countryWithHighestNumberOfCities();
            case 7 -> generalMenu();
            default -> System.out.println("Invalid input!");
        }
    }

    private static void readAllCountry() {
        scanner.nextLine();
        System.out.println("Do you want to search for a specific country? (y/n)");
        switch (scanner.nextLine()) {
            case "y", "Y" -> {
                System.out.println("Enter the search term:");
                String search = scanner.nextLine();
                displayCountries(countryService.getAll(search));
            }
            case "n", "N" -> displayCountries(countryService.getAll());
            default -> {
                System.out.println("Invalid input!");
                readAllCountry();
                return;
            }
        }
        countryMenu();
    }

    private static void readCountry() {
        System.out.println("Enter the id of the country you want to read:");
        Long id = scanner.nextLong();
        Country country = countryService.getById(id);
        if (country == null) {
            System.out.println("Country with id " + id + " does not exist!");
            countryMenu();
            return;
        }
        displayCountries(List.of(country));
        countryMenu();
    }

    private static void deleteCountry() {
        displayCountries(countryService.getAll());
        System.out.println("Enter the id of the country you want to delete:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        Country country = countryService.getById(id);
        if (country == null) {
            System.out.println("Country with id " + id + " does not exist!");
            countryMenu();
            return;
        }

        if (countryService.delete(id))
            System.out.println("Country successfully deleted!");
        countryMenu();
    }

    private static void createCountry() {
        System.out.println("Enter the name of the country you want to create:");
        scanner.nextLine();
        String name = scanner.nextLine();
        if (countryService.save(new Country(name)))
            System.out.println("Country successfully created!");
        countryMenu();
    }

    private static void updateCountry() {
        displayCountries(countryService.getAll());
        System.out.println("Enter the id of the country you want to update:");
        scanner.nextLine();
        Long id = scanner.nextLong();
        Country country = countryService.getById(id);
        if (country == null) {
            System.out.println("Country with id " + id + " does not exist!");
            countryMenu();
            return;
        }
        System.out.println("Enter the new name of the country:");
        scanner.nextLine();
        String newName = scanner.nextLine();
        country.setName(newName);
        if (countryService.save(country))
            System.out.println("Country successfully updated!");
        countryMenu();
    }

    private static void countryWithHighestNumberOfCities() {
        Map<String, Object> result = countryService.getCountryWithHighestNumberOfCities();
        if (result.isEmpty()) {
            System.out.println("No countries found!");
            countryMenu();
            return;
        }

        System.out.println("+" + "-".repeat(48) + "+");
        System.out.printf("| %-5s | %-20s | %-15s |\n", "ID", "Name", "Cities Count");
        System.out.println("+" + "-".repeat(48) + "+");

        System.out.printf("| %-5d | %-20s | %-15d |\n", (Long) result.get("id"), result.get("name"), (Long) result.get("cities_count"));
        System.out.println("+" + "-".repeat(48) + "+");

        countryMenu();
    }

    private static void displayCountries(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            System.out.println("No countries found!");
            return;
        }

        System.out.println("+" + "-".repeat(30) + "+");
        System.out.printf("| %-5s | %-20s |\n", "ID", "Name");
        System.out.println("+" + "-".repeat(30) + "+");

        for (Country country : countries) {
            System.out.printf("| %-5d | %-20s |\n", country.getId(), country.getName());
            System.out.println("+" + "-".repeat(30) + "+");
        }
    }

    private static void displayAreas(List<Area> areas) {
        if (areas == null || areas.isEmpty()) {
            System.out.println("No areas found!");
            return;
        }

        System.out.println("+" + "-".repeat(30) + "+");
        System.out.printf("| %-5s | %-20s |\n", "ID", "Name");
        System.out.println("+" + "-".repeat(30) + "+");

        for (Area area : areas) {
            System.out.printf("| %-5d | %-20s |\n", area.getId(), area.getName());
            System.out.println("+" + "-".repeat(30) + "+");
        }
    }

    private static void displayCities(List<City> cities) {
        if (cities == null || cities.isEmpty()) {
            System.out.println("No cities found!");
            return;
        }

        System.out.println("+" + "-".repeat(58) + "+");
        System.out.printf("| %-5s | %-20s | %-25s |\n", "ID", "Name", "Country Name");
        System.out.println("+" + "-".repeat(58) + "+");

        for (City city : cities) {

            System.out.printf("| %-5d | %-20s | %-25s |\n", city.getId(), city.getName(), city.getCountry().getName());
            System.out.println("+" + "-".repeat(58) + "+");
        }
    }

    private static void displaySimpleEmployees(List<SimpleEmployeeDto> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees found!");
            return;
        }

        System.out.println("+" + "-".repeat(208) + "+");
        System.out.printf("| %-5s | %-30s | %-15s | %-10s | %-30s | %-20s | %-20s | %-32s | %-20s |\n",
                "ID", "Name", "JMBG", "Type", "Areas", "City and Country", "Employment Date", "Professional Qualification Level", "Fee Per Article");
        System.out.println("+" + "-".repeat(208) + "+");

        for (SimpleEmployeeDto employee : employees) {
            System.out.printf("| %-5d | %-30s | %-15s | %-10s | %-30s | %-20s | %-20s | %-32s | %-20s |\n",
                    employee.id(), employee.name(), employee.jmbg(), employee.type(),
                    employee.areaNames(),
                    employee.cityAndCountry(),
                    employee.employmentDate(),
                    employee.pql(),
                    employee.feePerArticle() == null ? "null" : employee.feePerArticle() / 100.0);
            System.out.println("+" + "-".repeat(208) + "+");
        }
    }

    public static void displayEmployeeDetails(EmployeeDto employee) {
        if (employee == null) {
            System.out.println("Employee not found!");
            return;
        }

        String[] columnNames = {"ID", "NAME", "LAST NAME", "JMBG", "ADDRESS", "PHONE NUMBER", "BIRTH DATE",
                "EMPLOYMENT DATE", "TYPE", "PROFESSIONAL QUALIFICATION LEVEL", "FEE PER ARTICLE (Cents)",
                "CITY NAME", "COUNTRY NAME", "AREA NAMES"};


        String[] values = {String.valueOf(employee.id()), employee.name(), employee.lastName(), employee.jmbg(),
                employee.address(), employee.phoneNumber(),
                String.valueOf(employee.birthDate()), String.valueOf(employee.employmentDate()),
                String.valueOf(employee.type()), employee.professionalQualificationLevel(),
                String.valueOf(employee.feePerArticle()), employee.cityName(), employee.countryName(),
                employee.areaNames()};

        int columnWidth = 40;

        printHorizontalLine();
        for (int i = 0; i < columnNames.length; i++) {
            System.out.printf("| %-" + columnWidth + "s | %-" + columnWidth + "s |\n", columnNames[i], values[i] == null ? "" : values[i]);
            printHorizontalLine();
        }
    }

    private static void printHorizontalLine() {
        System.out.println("+" + "-".repeat(85) + "+");
    }
}