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
@Table(name = "genres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Genre name is required")
    @Size(max = 100, message = "Genre name cannot exceed 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String characteristics;

    @Min(value = 1, message = "Energy minimum must be at least 1")
    @Max(value = 10, message = "Energy minimum cannot exceed 10")
    @Column(name = "energy_min")
    private Integer energyMin = 1;

    @Min(value = 1, message = "Energy maximum must be at least 1")
    @Max(value = 10, message = "Energy maximum cannot exceed 10")
    @Column(name = "energy_max")
    private Integer energyMax = 10;

    @Min(value = 1, message = "Valence minimum must be at least 1")
    @Max(value = 10, message = "Valence minimum cannot exceed 10")
    @Column(name = "valence_min")
    private Integer valenceMin = 1;

    @Min(value = 1, message = "Valence maximum must be at least 1")
    @Max(value = 10, message = "Valence maximum cannot exceed 10")
    @Column(name = "valence_max")
    private Integer valenceMax = 10;

    @Min(value = 1, message = "Arousal minimum must be at least 1")
    @Max(value = 10, message = "Arousal minimum cannot exceed 10")
    @Column(name = "arousal_min")
    private Integer arousalMin = 1;

    @Min(value = 1, message = "Arousal maximum must be at least 1")
    @Max(value = 10, message = "Arousal maximum cannot exceed 10")
    @Column(name = "arousal_max")
    private Integer arousalMax = 10;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Song> songs = new ArrayList<>();

    // Helper methods
    public boolean isCompatibleWithMood(int energy, int valence, int arousal) {
        return energy >= energyMin && energy <= energyMax &&
               valence >= valenceMin && valence <= valenceMax &&
               arousal >= arousalMin && arousal <= arousalMax;
    }

    public double getCompatibilityScore(int energy, int valence, int arousal) {
        // Calculate how well this genre matches the given mood parameters
        double energyScore = 1.0 - Math.abs(((energyMin + energyMax) / 2.0) - energy) / 10.0;
        double valenceScore = 1.0 - Math.abs(((valenceMin + valenceMax) / 2.0) - valence) / 10.0;
        double arousalScore = 1.0 - Math.abs(((arousalMin + arousalMax) / 2.0) - arousal) / 10.0;
        
        return (energyScore + valenceScore + arousalScore) / 3.0;
    }

    public void addSong(Song song) {
        songs.add(song);
        song.setGenre(this);
    }
}
