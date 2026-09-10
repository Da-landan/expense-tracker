package com.dalandan.expense_tracker.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();
    }

    @Test
    void rejectsNegativeAmount() {

        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("-100"));
        request.setCategory("Food");

        Set<ConstraintViolation<ExpenseRequest>> violations =
                validator.validate(request);

        boolean hasAmountError = violations.stream()
                .anyMatch(violation ->
                        violation.getMessage()
                                .equals("Amount must be greater than 0")
                );

        assertTrue(hasAmountError);
    }

    @Test
    void rejectsBlankCategory() {

        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("100"));
        request.setCategory("");

        Set<ConstraintViolation<ExpenseRequest>> violations =
                validator.validate(request);

        boolean hasCategoryError = violations.stream()
                .anyMatch(violation ->
                        violation.getMessage()
                                .equals("Category is required")
                );

        assertTrue(hasCategoryError);
    }
}