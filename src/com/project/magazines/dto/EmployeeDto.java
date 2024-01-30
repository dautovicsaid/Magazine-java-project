package com.project.magazines.dto;

import com.project.magazines.entity.City;
import com.project.magazines.enumeration.EmployeeType;

import java.sql.Date;

public record EmployeeDto (
     Long id,
     String name,
     String lastName,
     String jmbg,
     String address,
     String phoneNumber,
     Date birthDate,
     Date employmentDate,
     EmployeeType type,
     String professionalQualificationLevel,
     Integer feePerArticle,
     String cityName,
     String countryName,
     String areaNames) {

    }

