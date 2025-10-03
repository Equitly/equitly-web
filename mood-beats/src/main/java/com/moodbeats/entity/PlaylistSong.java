package com.moodbeats.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist_songs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"playlist_id", "position"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Playlist is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @NotNull(message = "Song is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @NotNull(message = "Position is required")
    @Min(value = 1, message = "Position must be at least 1")
    @Column(nullable = false)
    private Integer position;

    @CreatedDate
    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    // Helper methods
    public String getDisplayInfo() {
        if (song != null) {
            return String.format("#%d - %s", position, song.getDisplayName());
        }
        return String.format("Position #%d", position);
    }

    public boolean isFirstSong() {
        return position != null && position == 1;
    }

    public boolean isLastSong() {
        return playlist != null && position != null && position == playlist.getSongCount();
    }
}
