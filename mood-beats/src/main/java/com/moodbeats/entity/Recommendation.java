package com.moodbeats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Mood analysis is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_analysis_id", nullable = false)
    private MoodAnalysis moodAnalysis;

    @NotNull(message = "Song is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @DecimalMin(value = "0.00", message = "Recommendation score must be at least 0.00")
    @DecimalMax(value = "1.00", message = "Recommendation score cannot exceed 1.00")
    @Column(name = "recommendation_score", precision = 3, scale = 2)
    private BigDecimal recommendationScore;

    @Min(value = 1, message = "User rating must be at least 1")
    @Max(value = 5, message = "User rating cannot exceed 5")
    @Column(name = "user_rating")
    private Integer userRating;

    @Column(name = "user_feedback", columnDefinition = "TEXT")
    private String userFeedback;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public boolean hasUserRating() {
        return userRating != null;
    }

    public boolean hasUserFeedback() {
        return userFeedback != null && !userFeedback.trim().isEmpty();
    }

    public boolean isHighlyRated() {
        return userRating != null && userRating >= 4;
    }

    public boolean isPoorlyRated() {
        return userRating != null && userRating <= 2;
    }

    public boolean isHighConfidenceRecommendation() {
        return recommendationScore != null && recommendationScore.compareTo(new BigDecimal("0.80")) >= 0;
    }

    public boolean isLowConfidenceRecommendation() {
        return recommendationScore != null && recommendationScore.compareTo(new BigDecimal("0.50")) < 0;
    }

    public String getRecommendationStrength() {
        if (recommendationScore == null) {
            return "Unknown";
        }
        
        double score = recommendationScore.doubleValue();
        if (score >= 0.9) return "Excellent Match";
        if (score >= 0.8) return "Great Match";
        if (score >= 0.7) return "Good Match";
        if (score >= 0.6) return "Fair Match";
        if (score >= 0.5) return "Weak Match";
        return "Poor Match";
    }

    public String getRatingDescription() {
        if (userRating == null) {
            return "Not rated";
        }
        
        return switch (userRating) {
            case 5 -> "Loved it";
            case 4 -> "Really liked it";
            case 3 -> "It was okay";
            case 2 -> "Didn't like it";
            case 1 -> "Hated it";
            default -> "Invalid rating";
        };
    }

    public void rateSong(int rating, String feedback) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.userRating = rating;
        this.userFeedback = feedback;
    }

    public double getAccuracyScore() {
        if (userRating == null || recommendationScore == null) {
            return 0.0;
        }
        
        // Convert user rating (1-5) to a score (0-1)
        double normalizedUserRating = (userRating - 1) / 4.0;
        double predictedScore = recommendationScore.doubleValue();
        
        // Calculate accuracy as inverse of the difference
        return 1.0 - Math.abs(normalizedUserRating - predictedScore);
    }
}
