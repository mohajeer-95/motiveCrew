package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementSeenRequest {

    @NotNull
    private Long userId;
}


