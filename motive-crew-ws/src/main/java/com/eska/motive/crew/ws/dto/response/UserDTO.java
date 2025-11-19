package com.eska.motive.crew.ws.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * User response DTO
 * 
 * @author Motive Crew Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String position;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDate joinedDate;
    private String currentMonthStatus;
    private LocalDate lastPaymentDate;
    private Integer eventsJoined;
    private BigDecimal totalContribution;
}

