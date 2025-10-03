package com.moodbeats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoodAnalysisRequest {

    @NotBlank(message = "Mood description is required")
    @Size(min = 5, max = 1000, message = "Mood description must be between 5 and 1000 characters")
    private String moodDescription;

    @Size(max = 100, message = "Context cannot exceed 100 characters")
    private String context; // Optional: "working", "exercising", "relaxing", etc.

    private Boolean includeExplicitContent = false;

    private Integer maxRecommendations = 10;
}
