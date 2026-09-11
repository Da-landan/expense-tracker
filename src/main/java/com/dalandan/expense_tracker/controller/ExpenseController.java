package com.dalandan.expense_tracker.controller;

import com.dalandan.expense_tracker.dto.ExpenseRequest;
import com.dalandan.expense_tracker.model.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.dalandan.expense_tracker.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    //GET
    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {

        List<Expense> expenses = expenseService.getExpensesForCurrentUser(category, startDate, endDate);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {

        Expense expense = expenseService.getExpenseById(id);

        return ResponseEntity.ok(expense);
    }

    @GetMapping("/expenses/summary")
    public ResponseEntity<Map<String, BigDecimal>> getExpenseSummary(
            @RequestParam(required = false) String month
    ){
        Map<String, BigDecimal> summary = expenseService.getMonthlySummary(month);

        return ResponseEntity.ok(summary);
    }

    //POST
    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseRequest request) {

        Expense savedExpense = expenseService.createExpense(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    //PUT
    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {

        Expense updatedExpense = expenseService.updateExpense(id, request);

        return ResponseEntity.ok(updatedExpense);
    }

    //DELETE
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }
}