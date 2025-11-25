package com.eska.motive.crew.ws.dto.response;

import com.eska.motive.crew.ws.enums.AnnouncementAudience;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AnnouncementDTO {
    Long id;
    String title;
    String message;
    String imageUrl;
    AnnouncementAudience audience;
    Long teamId;
    String teamName;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime expiresAt;
}


