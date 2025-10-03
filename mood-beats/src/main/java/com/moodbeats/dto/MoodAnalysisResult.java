package com.moodbeats.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoodAnalysisResult {

    private Long id;
    private String moodDescription;
    private List<String> primaryEmotions;
    private Integer energyLevel;
    private Integer arousalLevel;
    private Integer valence;
    private String moodSummary;
    private List<String> recommendedGenres;
    private List<MoodRecommendation> recommendations;
    private LocalDateTime createdAt;
    private String analysisInsight;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MoodRecommendation {
        private Long songId;
        private String title;
        private String artist;
        private String genre;
        private Double matchScore;
        private String matchReason;
        private String previewUrl;
        private String formattedDuration;
    }
}
