package com.project.magazines;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Area;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.service.AreaService;
import com.project.magazines.service.CountryService;
import com.project.magazines.service.CityService;


import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DatabaseConnection dbconn = new DatabaseConnection("root", "password", "127.0.0.1", "3306", "magazines");
    private static final CountryService countryService = new CountryService(dbconn);
    private static final AreaService areaService = new AreaService(dbconn);
    private static final CityService cityService = new CityService(dbconn);

    public static void main(String[] args) {


        /*
        AreaService areaService = new AreaService(dbconn);
        EmployeeService employeeService = new EmployeeService(dbconn);*/

        /*countryService.create(new java.com.project.magazines.entity.Country("Srbija"));
        countryService.create(new java.com.project.magazines.entity.Country("Hrvatska"));
        countryService.create(new java.com.project.magazines.entity.Country("Bosna i Hercegovina"));
        countryService.create(new java.com.project.magazines.entity.Country("Crna Gora"));
        countryService.update(new java.com.project.magazines.entity.Country("Srbija"), countryService.findId(new java.com.project.magazines.entity.Country("Srbijaa")));
        countryService.create(new java.com.project.magazines.entity.Country("Test drzava"));
        cityService.create(new City("Beograd", new java.com.project.magazines.entity.Country("Srbija")));
        cityService.create(new City("Novi Sad", new java.com.project.magazines.entity.Country("Srbija")));
        cityService.create(new City("Niš", new java.com.project.magazines.entity.Country("Srbija")));
        countryService.delete(countryService.findId(new Country("Test drzava")));
        countryService.delete(countryService.findId(new Country("Srbija")));

         */

        /*countryService.create(new Country("Srbija"));
        countryService.update(new Country("Srbija"), countryService.findId(new Country("Srbija")));

        areaService.create(new Area("Sport"));
        areaService.create(new Area("Svijet"));
        areaService.create(new Area("Lifestyle"));
        areaService.create(new Area("Kultura"));
        areaService.create(new Area("Zabava"));
        areaService.create(new Area("Politika"));
        areaService.create(new Area("Ekonomija"));
        areaService.create(new Area("Hronika"));*/
        //countryService.getAll("na").forEach(System.out::println);
        //cityService.create(new City("Test grad 2", new Country("Ničija zemlja")));
//        cityService.getAll("ni").forEach(System.out::println);
        //areaService.getAll().forEach(System.out::println);
        //System.out.println(employeeService.findId("1234567891234"));
        /*employeeService.create(new Employee(
                "Said",
                "Dautovic",
                "1234567891234",
                "Adresa 1",
                "061111111",
                Date.valueOf("1999-01-01"),
                Date.valueOf("2021-01-01"),
                EmployeeType.REGULAR,
                "Level 2",
                new City("Novi Sad", new Country("Srbija"))));*/
        //System.out.println(employeeService.getAll("dsa", EmployeeType.REGULAR));

        /*areaService.save(new Area("Elektronika"));
        System.out.println(areaService.getAll());
        System.out.println("=".repeat(50));
        System.out.println(areaService.getAll("ik"));*/
        /*System.out.println(cityService.getAll());
        System.out.println("=".repeat(50));
        System.out.println(cityService.getAll("ni"));*/
        //countryService.save(new Country(countryService.findId(new Country("Srbija 2")), "Srbija"));
        generalMenu();
    }

    private static void generalMenu() {
        System.out.println("""
                +----------------------------+
                | Please select the table:    |
                +----------------------------+
                | 1. Country                  |
                | 2. City                     |
                | 3. Area                     |
                +----------------------------+
                """);

        int result = scanner.nextInt();
        switch (result) {
            case 1 -> countryMenu();
            case 2 -> cityMenu();
            case 3 -> areaMenu();
            default -> System.out.println("Invalid input!");
        }
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

    private static void  updateCityCountry(City city) {
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
        String newName = scanner.nextLine();
        scanner.nextLine();
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
}