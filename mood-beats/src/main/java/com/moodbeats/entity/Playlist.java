package com.moodbeats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_analysis_id")
    private MoodAnalysis moodAnalysis;

    @NotBlank(message = "Playlist name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<PlaylistSong> playlistSongs = new ArrayList<>();

    // Helper methods
    public int getSongCount() {
        return playlistSongs.size();
    }

    public long getTotalDurationMs() {
        return playlistSongs.stream()
                .filter(ps -> ps.getSong().getDurationMs() != null)
                .mapToLong(ps -> ps.getSong().getDurationMs())
                .sum();
    }

    public String getFormattedDuration() {
        long totalMs = getTotalDurationMs();
        long totalSeconds = totalMs / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        if (minutes >= 60) {
            long hours = minutes / 60;
            minutes = minutes % 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        
        return String.format("%d:%02d", minutes, seconds);
    }

    public void addSong(Song song) {
        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(this);
        playlistSong.setSong(song);
        playlistSong.setPosition(playlistSongs.size() + 1);
        playlistSongs.add(playlistSong);
    }

    public void addSongAtPosition(Song song, int position) {
        // Adjust positions of existing songs
        playlistSongs.stream()
                .filter(ps -> ps.getPosition() >= position)
                .forEach(ps -> ps.setPosition(ps.getPosition() + 1));

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(this);
        playlistSong.setSong(song);
        playlistSong.setPosition(position);
        playlistSongs.add(playlistSong);
    }

    public void removeSong(Song song) {
        PlaylistSong toRemove = playlistSongs.stream()
                .filter(ps -> ps.getSong().equals(song))
                .findFirst()
                .orElse(null);
                
        if (toRemove != null) {
            int removedPosition = toRemove.getPosition();
            playlistSongs.remove(toRemove);
            
            // Adjust positions of remaining songs
            playlistSongs.stream()
                    .filter(ps -> ps.getPosition() > removedPosition)
                    .forEach(ps -> ps.setPosition(ps.getPosition() - 1));
        }
    }

    public List<Song> getSongs() {
        return playlistSongs.stream()
                .map(PlaylistSong::getSong)
                .toList();
    }

    public String getMoodBasedTitle() {
        if (moodAnalysis == null) {
            return name;
        }
        
        String moodDescription = moodAnalysis.getMoodDescription();
        if (moodDescription.length() > 50) {
            moodDescription = moodDescription.substring(0, 47) + "...";
        }
        
        return String.format("Mood: %s", moodDescription);
    }
}
