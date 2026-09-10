package com.dalandan.expense_tracker.service;

import com.dalandan.expense_tracker.dto.ExpenseRequest;
import com.dalandan.expense_tracker.exception.ResourceNotFoundException;
import com.dalandan.expense_tracker.model.Expense;
import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.ExpenseRepository;
import com.dalandan.expense_tracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    //GET METHODS
    public Expense getExpenseById(Long id) {

        User currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found")
                );


        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Expense not found");
        }

        return expense;
    }

    public List<Expense> getExpensesForCurrentUser(String category, LocalDate startDate, LocalDate endDate){
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Only one date was provided
        if ((startDate == null) != (endDate == null)) {
            throw new IllegalArgumentException(
                    "Both startDate and endDate must be provided"
            );
        }

        Long userId = currentUser.getId();

        // Category + date range
        if (category != null && startDate != null && endDate != null) {

            return expenseRepository
                    .findByUserIdAndCategoryAndDateBetween(
                            userId,
                            category,
                            startDate,
                            endDate
                    );
        }

        // Category only
        if (category != null) {

            return expenseRepository
                    .findByUserIdAndCategory(
                            userId,
                            category
                    );
        }

        // Date range only
        if (startDate != null && endDate != null) {
            return expenseRepository
                    .findByUserIdAndDateBetween(
                            userId,
                            startDate,
                            endDate
                    );
        }

        // No filters
        return expenseRepository.findByUserId(userId);
    }

    public Map<String, BigDecimal> getMonthlySummary(LocalDate startDate, LocalDate endDate) {

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        List<Expense> expenses = expenseRepository.findByUserIdAndDateBetween(
                        currentUser.getId(),
                        startDate,
                        endDate
                );

        Map<String, BigDecimal> summary = new LinkedHashMap<>();

        for (Expense expense : expenses) {

            summary.merge(
                    expense.getCategory(),
                    expense.getAmount(),
                    BigDecimal::add
            );
        }

        return summary;
    }

    //POST METHODS
    public Expense createExpense(ExpenseRequest request){
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Expense expense = new Expense(
                request.getAmount(),
                request.getCategory(),
                request.getDescription(),
                request.getDate(),
                currentUser
        );

        return expenseRepository.save(expense);
    }

    //PUT METHODS
    public Expense updateExpense(Long id, ExpenseRequest request){
        User currentUser = getCurrentUser();

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Expense expense = expenseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense not found")
        );

        // Ownership check
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Expense not found");
        }

        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());

        return expenseRepository.save(expense);
    }

    //DELETE METHODS
    public boolean deleteExpense(Long id){
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Expense expense = expenseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense not found"));

        // Ownership check
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Expense not found");
        }

        expenseRepository.deleteById(id);

        return true;
    }

    //HELPER METHOD
    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElse(null);
    }
}