package com.project.magazines.enumeration;

import com.project.magazines.entity.City;

import java.sql.Date;

public class Employee {
    private Long id;
    private String name;
    private String lastName;
    private String jmbg;
    private String address;
    private String phoneNumber;
    private Date birthDate;
    private Date employmentDate;
    private EmployeeType type;
    private String professionalQualificationLevel;
    private int feePerArticle;
    private City city;

    public Employee(Long id, String name, String lastName, String jmbg, String address,
                    String phoneNumber, Date birthDate, Date employmentDate, EmployeeType employeeType,
                    String professionalQualificationLevel, City city) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.employmentDate = employmentDate;
        this.type = employeeType;
        this.professionalQualificationLevel = professionalQualificationLevel;
        this.city = city;
    }

    public Employee( String name, String lastName, String jmbg, String address,
                     String phoneNumber, Date birthDate, Date employmentDate,
                     EmployeeType employeeType, String professionalQualificationLevel, City city) {
        this.name = name;
        this.lastName = lastName;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.employmentDate = employmentDate;
        this.type = employeeType;
        this.professionalQualificationLevel = professionalQualificationLevel;
        this.city = city;
    }

    public Employee(Long id, String name, String lastName, String jmbg, String address,
                    String phoneNumber, Date birthDate, Date employmentDate,
                    EmployeeType employeeType, City city) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.employmentDate = employmentDate;
        this.type = employeeType;
        this.city = city;
    }

    public Employee(String name, String lastName, String jmbg, String address,
                    String phoneNumber, Date birthDate, Date employmentDate,
                    EmployeeType employeeType, City city) {
        this.name = name;
        this.lastName = lastName;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.employmentDate = employmentDate;
        this.type = employeeType;
        this.city = city;
    }

    public Employee(Long id, String name, String lastName, String jmbg, String address,
                    String phoneNumber, Date birthDate, EmployeeType employeeType,
                    int feePerArticle, City city) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.type = employeeType;
        this.feePerArticle = feePerArticle;
        this.city = city;
    }

    public Employee(String name, String lastName, String jmbg, String address,
                    String phoneNumber, Date birthDate, EmployeeType employeeType,
                    int feePerArticle, City city) {
        this.name = name;
        this.lastName = lastName;
        this.jmbg = jmbg;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.type = employeeType;
        this.feePerArticle = feePerArticle;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    public String getProfessionalQualificationLevel() {
        return professionalQualificationLevel;
    }

    public void setProfessionalQualificationLevel(String professionalQualificationLevel) {
        this.professionalQualificationLevel = professionalQualificationLevel;
    }

    public int getFeePerArticle() {
        return feePerArticle;
    }

    public void setFeePerArticle(int feePerArticle) {
        this.feePerArticle = feePerArticle;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return """
                {
                    "id": %d,
                    "name": "%s",
                    "lastName": "%s",
                    "jmbg": "%s",
                    "address": "%s",
                    "phoneNumber": "%s",
                    "birthDate": "%s",
                    "employmentDate": "%s",
                    "employeeType": "%s",
                    "professionalQualificationLevel": "%s",
                    "feePerArticle": %d,
                    "city": %s
                }
                """.formatted(id, name, lastName, jmbg, address, phoneNumber, birthDate,
                employmentDate, type, professionalQualificationLevel, feePerArticle, city);
    }
}
