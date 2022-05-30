package com.example.easyrepot;

import com.example.easyreport.ReportColumn;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportTest {

    @ReportColumn(title = "Name")
    private String name;

    @ReportColumn(title = "Date Of Birth")
    private LocalDate dateOfBirth;

    @ReportColumn(title = "Age")
    private Integer age;

    @ReportColumn(title = "Creation")
    private LocalDateTime creation;

    public ReportTest(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public ReportTest(String name, Integer age, LocalDate dateOfBirth, LocalDateTime creation) {
        this.name = name;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.creation = creation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }
}
