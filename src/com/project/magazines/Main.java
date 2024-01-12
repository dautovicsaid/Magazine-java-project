package com.project.magazines;

import com.project.magazines.connection.DatabaseConnection;
import com.project.magazines.entity.Area;
import com.project.magazines.entity.City;
import com.project.magazines.entity.Country;
import com.project.magazines.enumeration.Employee;
import com.project.magazines.enumeration.EmployeeType;
import com.project.magazines.service.AreaService;
import com.project.magazines.service.CityService;
import com.project.magazines.service.CountryService;
import com.project.magazines.service.EmployeeService;

import java.sql.Date;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection dbconn = new DatabaseConnection("root", "password", "127.0.0.1", "3306", "magazines");

        CountryService countryService = new CountryService(dbconn);
        CityService cityService = new CityService(dbconn);
        AreaService areaService = new AreaService(dbconn);
        EmployeeService employeeService = new EmployeeService(dbconn);

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
        employeeService.create(new Employee(
                "Said",
                "Dautovic",
                "1234567891234",
                "Adresa 1",
                "061111111",
                Date.valueOf("1999-01-01"),
                Date.valueOf("2021-01-01"),
                EmployeeType.REGULAR,
                "Level 2",
                new City("Novi Sad", new Country("Srbija"))));


    }
}