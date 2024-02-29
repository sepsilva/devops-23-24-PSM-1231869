package com.greglturnquist.payroll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    private String validEmployeeFirstName = "John";
    private String nullString = null;
    private String emptyString = "";
    private String validEmployeeLastName = "Man";
    private String validJobDescription = "Man with family";
    private String validJobTitle = "Family man";
    private int validJobYears = 10;
    private int negativeJobYears = -10;

    /**
     * Test construction of new Employee obj with valid parameters. Sees if illegalArgumentException isn't thrown
     */
    @Test
    void constructEmployeeWithValidAttributes () {
        assertDoesNotThrow(() ->new Employee(validEmployeeFirstName, validEmployeeLastName, validJobDescription, validJobTitle, validJobYears));
    }

    /**
     * Test construction of new Employee obj with nullString as first name and rest valid arguments. Sees if IllegalArgumentException is thrown
     */
    @Test
    void constructEmployeeNullFirstName () {
        assertThrows(IllegalArgumentException.class, () -> new Employee(nullString, validEmployeeLastName, validJobDescription, validJobTitle, validJobYears));
    }

    /**
     * Test construction of new Employee obj with emptyString as last name and rest valid arguments. Sees if IllegalArgumentException is thrown
     */
    @Test
    void constructEmployeeEmptyLastName () {
        assertThrows(IllegalArgumentException.class, () -> new Employee(validEmployeeFirstName, emptyString, validJobDescription, validJobTitle, validJobYears));
    }

    /**
     * Test construction of new Employee obj with negativeJobYears and rest valid arguments. Sess if IllegalArgumentException is thrown
     */
    @Test
    void constructEmployeeWithNegativeJobYears () {
        assertThrows(IllegalArgumentException.class, () -> new Employee(validEmployeeFirstName, validEmployeeLastName, validJobDescription, validJobTitle, negativeJobYears));
    }
}