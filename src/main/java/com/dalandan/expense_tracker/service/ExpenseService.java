package com.dalandan.expense_tracker.service;

import com.dalandan.expense_tracker.dto.*;
import com.dalandan.expense_tracker.exception.*;
import com.dalandan.expense_tracker.model.*;
import com.dalandan.expense_tracker.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
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

    // --- GET METHODS ---
    public ExpenseResponse getExpenseById(Long id) {

        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found")
                );


        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Expense not found");
        }

        return toResponse(expense);
    }

    public List<ExpenseResponse> getExpensesForCurrentUser(String category, LocalDate startDate, LocalDate endDate){
        User currentUser = getCurrentUser();

        // Only one date was provided
        if ((startDate == null) != (endDate == null)) {
            throw new IllegalArgumentException(
                    "Both startDate and endDate must be provided"
            );
        }

        Long userId = currentUser.getId();

        // Category + date range
        if (category != null && startDate != null) {
            return toResponseList(
                    expenseRepository.findByUserIdAndCategoryAndDateBetween(
                            userId,
                            category,
                            startDate,
                            endDate
                    ));
        }

        // Category only
        if (category != null) {
            return toResponseList(
                    expenseRepository.findByUserIdAndCategory(
                            userId,
                            category
                    ));
        }

        // Date range only
        if (startDate != null && endDate != null) {
            return toResponseList(
                    expenseRepository.findByUserIdAndDateBetween(
                            userId,
                            startDate,
                            endDate
                    ));
        }

        // No filters
        return toResponseList(expenseRepository.findByUserId(userId));
    }

    public Map<String, BigDecimal> getMonthlySummary(String month) {

        User currentUser = getCurrentUser();

        YearMonth targetMonth;

        if (month == null || month.isBlank()) {
            targetMonth = YearMonth.now();
        } else {
            try{
                targetMonth = YearMonth.parse(month);
            }
            catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("Month must use YYYY-MM format");
            }
        }

        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();

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

    // ----POST METHODS ---
    public ExpenseResponse createExpense(ExpenseRequest request){
        User currentUser = getCurrentUser();

        Expense expense = new Expense(
                request.getAmount(),
                request.getCategory(),
                request.getDescription(),
                request.getDate(),
                currentUser
        );

        Expense savedExpense = expenseRepository.save(expense);


        return toResponse(expense);
    }

    // --- PUT METHODS ---
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request){
        User currentUser = getCurrentUser();

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

        Expense savedExpense = expenseRepository.save(expense);

        return toResponse(savedExpense);
    }

    // --- DELETE METHODS ---
    public void deleteExpense(Long id){
        User currentUser = getCurrentUser();

        Expense expense = expenseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense not found"));

        // Ownership check
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Expense not found");
        }

        expenseRepository.deleteById(id);
    }

    //HELPER METHOD
    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid authentication")
                );
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getDate()
        );
    }

    private List<ExpenseResponse> toResponseList(List<Expense> expenses) {
        return expenses.stream()
                .map(this::toResponse)
                .toList();
    }
}