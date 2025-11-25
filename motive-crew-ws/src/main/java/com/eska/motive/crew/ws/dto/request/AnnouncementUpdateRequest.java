package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 4000)
    private String message;

    @Size(max = 500)
    private String imageUrl;

    @NotBlank
    private String audience;

    private Long teamId;

    @Future(message = "expiresAt must be in the future")
    private LocalDateTime expiresAt;
}


