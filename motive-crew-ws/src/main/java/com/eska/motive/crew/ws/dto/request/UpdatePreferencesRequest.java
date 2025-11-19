package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update preferences request DTO
 * 
 * @author Motive Crew Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferencesRequest {

    private Boolean notificationsEnabled;

    private Boolean darkMode;

    @Size(max = 10, message = "Language code must not exceed 10 characters")
    private String language;

    private Boolean autoLogin;

    private String defaultMonth; // CURRENT, PREVIOUS, CUSTOM
}

