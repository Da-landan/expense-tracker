package com.dalandan.expense_tracker.service;

import com.dalandan.expense_tracker.model.Expense;
import com.dalandan.expense_tracker.model.User;
import com.dalandan.expense_tracker.repository.ExpenseRepository;
import com.dalandan.expense_tracker.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

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

    public void createExpense(){

    }

    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElse(null);
    }
}