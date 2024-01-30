package com.project.magazines.dto;

import com.project.magazines.enumeration.EmployeeType;

import java.util.Date;

public record SimpleEmployeeDto(
        Long id,
        String name,
        String jmbg,
        EmployeeType type,
        String areaNames,
        Date employmentDate,
        String pql,
        Integer feePerArticle) {
}
