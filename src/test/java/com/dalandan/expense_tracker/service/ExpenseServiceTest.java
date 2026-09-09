package com.dalandan.expense_tracker.service;

import com.dalandan.expense_tracker.exception.ResourceNotFoundException;
import com.dalandan.expense_tracker.model.Expense;
import com.dalandan.expense_tracker.model.User;

import com.dalandan.expense_tracker.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private ExpenseRepository expenseRepository;
    private UserRepository userRepository;
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {

        expenseRepository = mock(ExpenseRepository.class);
        userRepository = mock(UserRepository.class);

        expenseService = new ExpenseService(
                expenseRepository,
                userRepository
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "lance",
                        null
                )
        );
    }

    @Test
    void getExpenseById_returnsExpense_whenUserOwnsExpense() {

        // Arrange
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("lance");

        Expense expense = mock(Expense.class);
        when(expense.getId()).thenReturn(10L);
        when(expense.getUser()).thenReturn(user);

        when(userRepository.findByUsername("lance"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));

        // Act
        Expense result = expenseService.getExpenseById(10L);

        // Assert
        assertEquals(10L, result.getId());
    }

    @Test
    void getExpenseById_throwsException_whenExpenseDoesNotExist() {

        // Arrange
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("lance");

        when(userRepository.findByUsername("lance"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.getExpenseById(99L)
        );
    }
}