package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Create event request DTO
 * 
 * @author Motive Crew Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

    @NotBlank(message = "Event name is required")
    @Size(max = 200, message = "Event name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Event type is required")
    private String type; // FOOD, CAFÉ, OUTING, PURCHASE, OTHER

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    private LocalTime eventTime;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private BigDecimal estimatedCost;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
}

