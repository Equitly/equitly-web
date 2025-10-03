package com.moodbeats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Song title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Artist name is required")
    @Size(max = 255, message = "Artist name cannot exceed 255 characters")
    @Column(nullable = false)
    private String artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Min(value = 1, message = "Energy level must be at least 1")
    @Max(value = 10, message = "Energy level cannot exceed 10")
    @Column(name = "energy_level")
    private Integer energyLevel = 5;

    @Min(value = 1, message = "Valence must be at least 1")
    @Max(value = 10, message = "Valence cannot exceed 10")
    private Integer valence = 5;

    @Min(value = 1, message = "Arousal must be at least 1")
    @Max(value = 10, message = "Arousal cannot exceed 10")
    private Integer arousal = 5;

    @Min(value = 60, message = "Tempo must be at least 60 BPM")
    @Max(value = 200, message = "Tempo cannot exceed 200 BPM")
    private Integer tempo;

    @Min(value = 1000, message = "Duration must be at least 1 second")
    @Column(name = "duration_ms")
    private Long durationMs;

    @Size(max = 50, message = "Spotify ID cannot exceed 50 characters")
    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "preview_url", columnDefinition = "TEXT")
    private String previewUrl;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlaylistSong> playlistSongs = new ArrayList<>();

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recommendation> recommendations = new ArrayList<>();

    // Helper methods
    public String getDisplayName() {
        return String.format("%s - %s", title, artist);
    }

    public String getFormattedDuration() {
        if (durationMs == null) return "Unknown";
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public double getMoodMatchScore(MoodAnalysis moodAnalysis) {
        if (moodAnalysis.getEnergyLevel() == null || 
            moodAnalysis.getArousalLevel() == null || 
            moodAnalysis.getValence() == null) {
            return 0.0;
        }

        double energyScore = 1.0 - Math.abs(energyLevel - moodAnalysis.getEnergyLevel()) / 10.0;
        double valenceScore = 1.0 - Math.abs(valence - moodAnalysis.getValence()) / 10.0;
        double arousalScore = 1.0 - Math.abs(arousal - moodAnalysis.getArousalLevel()) / 10.0;

        // Weight the scores - energy and valence are more important for mood matching
        return (energyScore * 0.4) + (valenceScore * 0.4) + (arousalScore * 0.2);
    }

    public boolean isCompatibleWithMood(MoodAnalysis moodAnalysis) {
        return getMoodMatchScore(moodAnalysis) >= 0.6; // 60% compatibility threshold
    }

    public void addToPlaylist(PlaylistSong playlistSong) {
        playlistSongs.add(playlistSong);
        playlistSong.setSong(this);
    }

    public void addRecommendation(Recommendation recommendation) {
        recommendations.add(recommendation);
        recommendation.setSong(this);
    }

    // Audio feature categories for better recommendation
    public boolean isHighEnergy() {
        return energyLevel >= 7;
    }

    public boolean isPositive() {
        return valence >= 6;
    }

    public boolean isCalm() {
        return arousal <= 4;
    }

    public boolean isFastTempo() {
        return tempo != null && tempo >= 120;
    }
}
