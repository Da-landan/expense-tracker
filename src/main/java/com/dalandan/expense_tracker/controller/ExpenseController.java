package com.dalandan.expense_tracker.controller;

import com.dalandan.expense_tracker.dto.ExpenseRequest;
import com.dalandan.expense_tracker.model.Expense;
import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.ExpenseRepository;
import com.dalandan.expense_tracker.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseController(
            ExpenseRepository expenseRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/expenses")
    public List<Expense> getExpenses() {
        return expenseRepository.findAll();
    }

    @GetMapping("/expenses/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {

        return expenseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/expenses")
    public ResponseEntity<Expense> createExpense(
            @Valid @RequestBody ExpenseRequest request
    ) {

        User user = userRepository.findById(request.getUserId())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Expense expense = new Expense(
                request.getAmount(),
                request.getCategory(),
                request.getDescription(),
                request.getDate(),
                user
        );

        Expense savedExpense = expenseRepository.save(expense);

        return ResponseEntity.ok(savedExpense);
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request
    ) {

        Expense expense = expenseRepository.findById(id)
                .orElse(null);

        if (expense == null) {
            return ResponseEntity.notFound().build();
        }

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());

        Expense updatedExpense = expenseRepository.save(expense);

        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {

        if (!expenseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        expenseRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}