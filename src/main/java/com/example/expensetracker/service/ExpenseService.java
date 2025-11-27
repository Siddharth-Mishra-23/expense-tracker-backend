package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.exception.ResourceNotFoundException;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(ExpenseRequest request) {
        Expense expense = Expense.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .category(request.getCategory())
                .build();
        return expenseRepository.save(expense);
    }

    public Expense updateExpense(String id, ExpenseRequest request) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        existing.setUserId(request.getUserId());
        existing.setAmount(request.getAmount());
        existing.setDescription(request.getDescription());
        existing.setDate(request.getDate());
        existing.setCategory(request.getCategory());

        return expenseRepository.save(existing);
    }

    public void deleteExpense(String id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    public List<Expense> getExpenses(String userId,
                                     String category,
                                     LocalDate startDate,
                                     LocalDate endDate) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId is required for fetching expenses");
        }

        boolean hasCategory = StringUtils.hasText(category);
        boolean hasStart = (startDate != null);
        boolean hasEnd = (endDate != null);

        if (hasStart && hasEnd && hasCategory) {
            return expenseRepository.findByUserIdAndCategoryAndDateBetween(userId, category, startDate, endDate);
        } else if (hasStart && hasEnd) {
            return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
        } else if (hasCategory) {
            return expenseRepository.findByUserIdAndCategory(userId, category);
        } else {
            return expenseRepository.findByUserId(userId);
        }
    }
}
