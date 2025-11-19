package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePollRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    @Size(min = 2, message = "At least two options are required")
    private List<@NotBlank(message = "Option text is required") String> options;

    /**
     * ISO date string (yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss) representing poll deadline.
     */
    private String expiresAt;
}

