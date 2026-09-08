package com.dalandan.expense_tracker.service;

import com.dalandan.expense_tracker.dto.ExpenseRequest;
import com.dalandan.expense_tracker.model.Expense;
import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.ExpenseRepository;
import com.dalandan.expense_tracker.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
            return null;
        }

        Expense expense = expenseRepository.findById(id)
                .orElse(null);

        if (expense == null) {
            return null;
        }

        if (!expense.getUser().getId().equals(currentUser.getId())) {
            return null;
        }

        return expense;
    }

    public List<Expense> getExpensesForCurrentUser(){
        User currentUser = getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        return expenseRepository.findByUserId(currentUser.getId());
    }

    //POST METHODS
    public Expense createExpense(ExpenseRequest request){
        User currentUser = getCurrentUser();

        if (currentUser == null) {
            return null;
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
            return null;
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null) {
            return null;
        }

        // Ownership check
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            return null;
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
            return false;
        }

        Expense expense = expenseRepository.findById(id).orElse(null);

        if (expense == null) {
            return false;
        }

        // Ownership check
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            return false;
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