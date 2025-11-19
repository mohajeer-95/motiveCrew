package com.eska.motive.crew.ws.controller.v1;

import com.eska.motive.crew.contract.StatusCode;
import com.eska.motive.crew.ws.dto.request.CreateExpenseRequest;
import com.eska.motive.crew.ws.entity.Expense;
import com.eska.motive.crew.ws.entity.Event;
import com.eska.motive.crew.ws.entity.User;
import com.eska.motive.crew.ws.exception.InternalErrorException;
import com.eska.motive.crew.ws.exception.ResourceNotFoundException;
import com.eska.motive.crew.ws.exception.ValidationException;
import com.eska.motive.crew.ws.service.AuthService;
import com.eska.motive.crew.ws.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expense management controller
 * 
 * @author Motive Crew Team
 */
@RestController
@RequestMapping("/api/v1/expenses")
@Log4j2
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private AuthService authService;

    /**
     * Get all expenses
     * GET /api/v1/expenses
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExpenses(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long paidById,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Expense.ExpenseCategory expenseCategory = null;
            
            if (category != null) {
                try {
                    expenseCategory = Expense.ExpenseCategory.valueOf(category.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Invalid category
                }
            }
            
            Page<Expense> expenses = expenseService.getAllExpenses(month, year, expenseCategory, eventId, paidById, search, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.SUCCESS.getCode());
            response.put("message", "Expenses retrieved successfully");
            response.put("error", false);
            response.put("data", expenses.stream()
                    .map(this::buildExpenseResponse)
                    .toList());
            response.put("totalElements", expenses.getTotalElements());
            response.put("totalPages", expenses.getTotalPages());
            
            return ResponseEntity.status(HttpStatus.OK).body(response);
            
        } catch (Exception e) {
            log.error("Error getting expenses", e);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", StatusCode.INTERNAL_ERROR.getCode());
            response.put("message", "Error retrieving expenses: " + e.getMessage());
            response.put("error", true);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get expense by ID
     * GET /api/v1/expenses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getExpenseById(@PathVariable Long id)
            throws ResourceNotFoundException {
        Expense expense = expenseService.getExpenseById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Expense retrieved successfully");
        response.put("error", false);
        response.put("data", buildExpenseResponse(expense));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Create expense
     * POST /api/v1/expenses
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createExpense(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateExpenseRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        Expense expense = expenseService.createExpense(request, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Expense created successfully");
        response.put("error", false);
        response.put("data", buildExpenseResponse(expense));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update expense
     * PUT /api/v1/expenses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateExpense(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody CreateExpenseRequest request)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        Expense expense = expenseService.updateExpense(id, request, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Expense updated successfully");
        response.put("error", false);
        response.put("data", buildExpenseResponse(expense));
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Delete expense
     * DELETE /api/v1/expenses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteExpense(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id)
            throws ResourceNotFoundException, ValidationException, InternalErrorException {
        User currentUser = getCurrentUser(token);
        expenseService.deleteExpense(id, currentUser);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Expense deleted successfully");
        response.put("error", false);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get expense summary
     * GET /api/v1/expenses/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getExpenseSummary(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        ExpenseService.ExpenseSummary summary = expenseService.getExpenseSummary(month, year);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Expense summary retrieved");
        response.put("error", false);
        response.put("data", summary);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Get expenses by event
     * GET /api/v1/expenses/event/{eventId}
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Map<String, Object>> getExpensesByEvent(@PathVariable Long eventId)
            throws ResourceNotFoundException {
        List<Expense> expenses = expenseService.getExpensesByEvent(eventId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", StatusCode.SUCCESS.getCode());
        response.put("message", "Event expenses retrieved");
        response.put("error", false);
        response.put("data", expenses.stream()
                .map(this::buildExpenseResponse)
                .toList());
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private User getCurrentUser(String token) throws ResourceNotFoundException {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return authService.getCurrentUser(jwtToken);
    }

    private Map<String, Object> buildExpenseResponse(Expense expense) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", expense.getId());
        data.put("title", expense.getTitle());
        data.put("amount", expense.getAmount());
        data.put("category", expense.getCategory());
        data.put("description", expense.getDescription());
        data.put("expenseDate", expense.getExpenseDate());
        data.put("receiptUrl", expense.getReceiptUrl());
        data.put("createdAt", expense.getCreatedAt());
        data.put("updatedAt", expense.getUpdatedAt());
        data.put("paidBy", buildUserSummary(expense.getPaidBy()));
        data.put("createdBy", buildUserSummary(expense.getCreatedBy()));
        data.put("event", buildEventSummary(expense.getEvent()));
        return data;
    }

    private Map<String, Object> buildUserSummary(User user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", user.getId());
        summary.put("name", user.getName());
        summary.put("email", user.getEmail());
        summary.put("phone", user.getPhone());
        summary.put("role", user.getRole());
        summary.put("position", user.getPosition());
        summary.put("avatarUrl", user.getAvatarUrl());
        return summary;
    }

    private Map<String, Object> buildEventSummary(Event event) {
        if (event == null) {
            return null;
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", event.getId());
        summary.put("name", event.getName());
        summary.put("eventDate", event.getEventDate());
        summary.put("eventTime", event.getEventTime());
        summary.put("status", event.getStatus());
        summary.put("type", event.getType());
        summary.put("location", event.getLocation());
        return summary;
    }
}

