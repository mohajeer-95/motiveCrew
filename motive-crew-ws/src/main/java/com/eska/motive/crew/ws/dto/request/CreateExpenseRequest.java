package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Create expense request DTO
 * 
 * @author Motive Crew Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExpenseRequest {

    @NotBlank(message = "Expense title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    private String category; // FOOD, CAFÉ, PURCHASE, OTHER

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private Long eventId; // Optional

    @NotNull(message = "Paid by user ID is required")
    private Long paidById;

    @Size(max = 500, message = "Receipt URL must not exceed 500 characters")
    private String receiptUrl;
}

