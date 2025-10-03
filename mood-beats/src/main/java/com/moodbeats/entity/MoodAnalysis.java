package com.moodbeats.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mood_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class MoodAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Mood description is required")
    @Column(name = "mood_description", columnDefinition = "TEXT", nullable = false)
    private String moodDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "primary_emotions", columnDefinition = "jsonb")
    private JsonNode primaryEmotions;

    @Min(value = 1, message = "Energy level must be at least 1")
    @Max(value = 10, message = "Energy level cannot exceed 10")
    @Column(name = "energy_level")
    private Integer energyLevel;

    @Min(value = 1, message = "Arousal level must be at least 1")
    @Max(value = 10, message = "Arousal level cannot exceed 10")
    @Column(name = "arousal_level")
    private Integer arousalLevel;

    @Min(value = 1, message = "Valence must be at least 1")
    @Max(value = 10, message = "Valence cannot exceed 10")
    private Integer valence;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_analysis_raw", columnDefinition = "jsonb")
    private JsonNode aiAnalysisRaw;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "moodAnalysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "moodAnalysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recommendation> recommendations = new ArrayList<>();

    // Helper methods
    public String getMoodSummary() {
        return String.format("Energy: %d, Arousal: %d, Valence: %d", 
            energyLevel != null ? energyLevel : 0, 
            arousalLevel != null ? arousalLevel : 0, 
            valence != null ? valence : 0);
    }

    public boolean isHighEnergyMood() {
        return energyLevel != null && energyLevel >= 7;
    }

    public boolean isPositiveMood() {
        return valence != null && valence >= 6;
    }

    public boolean isCalmMood() {
        return arousalLevel != null && arousalLevel <= 4;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        playlist.setMoodAnalysis(this);
    }

    public void addRecommendation(Recommendation recommendation) {
        recommendations.add(recommendation);
        recommendation.setMoodAnalysis(this);
    }

    // Mood matching score for genre compatibility
    public double getMoodMatchScore(Genre genre) {
        if (energyLevel == null || arousalLevel == null || valence == null) {
            return 0.0;
        }
        return genre.getCompatibilityScore(energyLevel, arousalLevel, valence);
    }
}
