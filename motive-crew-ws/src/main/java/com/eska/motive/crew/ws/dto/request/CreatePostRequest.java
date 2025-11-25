package com.eska.motive.crew.ws.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    @Size(max = 5000, message = "Text must be less than 5000 characters")
    private String text;

    @Size(max = 500, message = "Image URL must be less than 500 characters")
    private String imageUrl;

    private String feedType; // "corporate" or "team"
}

