package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotePollRequest {

    @NotNull(message = "Option ID is required")
    private Long optionId;
}

