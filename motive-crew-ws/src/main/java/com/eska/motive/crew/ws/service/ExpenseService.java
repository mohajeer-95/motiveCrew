package com.eska.motive.crew.ws.service;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateExpenseRequest;
import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.Expense;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.repository.EventRepository;
import com.eska.motive.crew.ws.repository.ExpenseRepository;
import com.eska.motive.crew.ws.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing expenses
 * 
 * @author Motive Crew Team
 */
@Service
@Log4j2
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    /**
     * Get all expenses with filters
     */
    public Page<Expense> getAllExpenses(Integer month, Integer year, Expense.ExpenseCategory category,
                                       Long eventId, Long paidById, String search, Pageable pageable) {
        return expenseRepository.findByFilters(month, year, category, eventId, paidById, search, pageable);
    }

    /**
     * Get expense by ID
     */
    public Expense getExpenseById(Long id) throws ResourceNotFoundException {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
    }

    /**
     * Create a new expense
     */
    @Transactional
    public Expense createExpense(CreateExpenseRequest request, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can create expenses
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            // Get paid by user
            User paidBy = userRepository.findById(request.getPaidById())
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));

            // Get event if specified
            Event event = null;
            if (request.getEventId() != null) {
                event = eventRepository.findById(request.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
            }

            // Determine category
            Expense.ExpenseCategory category;
            try {
                category = Expense.ExpenseCategory.valueOf(request.getCategory().toUpperCase());
            } catch (IllegalArgumentException e) {
                category = Expense.ExpenseCategory.OTHER;
            }

            Expense expense = Expense.builder()
                    .title(request.getTitle())
                    .amount(request.getAmount())
                    .category(category)
                    .description(request.getDescription())
                    .expenseDate(request.getExpenseDate())
                    .event(event)
                    .paidBy(paidBy)
                    .createdBy(currentUser)
                    .receiptUrl(request.getReceiptUrl())
                    .build();

            return expenseRepository.save(expense);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating expense", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Update expense
     */
    @Transactional
    public Expense updateExpense(Long id, CreateExpenseRequest request, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can update expenses
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Expense expense = expenseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            // Update fields
            if (request.getTitle() != null) {
                expense.setTitle(request.getTitle());
            }
            if (request.getAmount() != null) {
                expense.setAmount(request.getAmount());
            }
            if (request.getCategory() != null) {
                try {
                    expense.setCategory(Expense.ExpenseCategory.valueOf(request.getCategory().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Keep existing category
                }
            }
            if (request.getDescription() != null) {
                expense.setDescription(request.getDescription());
            }
            if (request.getExpenseDate() != null) {
                expense.setExpenseDate(request.getExpenseDate());
            }
            if (request.getEventId() != null) {
                Event event = eventRepository.findById(request.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
                expense.setEvent(event);
            }
            if (request.getPaidById() != null) {
                User paidBy = userRepository.findById(request.getPaidById())
                        .orElseThrow(() -> new ResourceNotFoundException(StatusCode.USER_NOT_FOUND));
                expense.setPaidBy(paidBy);
            }
            if (request.getReceiptUrl() != null) {
                expense.setReceiptUrl(request.getReceiptUrl());
            }

            return expenseRepository.save(expense);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating expense", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Delete expense
     */
    @Transactional
    public void deleteExpense(Long id, User currentUser)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        try {
            // Only admin can delete expenses
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                throw new ValidationException(StatusCode.USER_ACCESS_DENIED);
            }

            Expense expense = expenseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));

            expenseRepository.delete(expense);

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting expense", e);
            throw new InternalErrorException(StatusCode.INTERNAL_ERROR);
        }
    }

    /**
     * Get expenses summary for a month
     */
    public ExpenseSummary getExpenseSummary(Integer month, Integer year) {
        BigDecimal totalSpent = expenseRepository.getTotalSpentByMonth(month, year);
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        List<Object[]> categoryData = expenseRepository.getExpensesByCategory(month, year);
        
        return ExpenseSummary.builder()
                .month(month)
                .year(year)
                .totalSpent(totalSpent)
                .expensesByCategory(categoryData)
                .build();
    }

    /**
     * Get expenses by event
     */
    public List<Expense> getExpensesByEvent(Long eventId) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(StatusCode.NOT_FOUND));
        return expenseRepository.findByEvent(event);
    }

    /**
     * Expense summary DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class ExpenseSummary {
        private Integer month;
        private Integer year;
        private BigDecimal totalSpent;
        private List<Object[]> expensesByCategory;
    }
}

