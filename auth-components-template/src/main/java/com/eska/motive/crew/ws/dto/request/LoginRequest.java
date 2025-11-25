package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login request DTO
 * 
 * @author Your Name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username/Email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}


