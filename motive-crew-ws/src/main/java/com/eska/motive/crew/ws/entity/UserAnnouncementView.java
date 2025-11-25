package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Tracks which announcements each user has seen.
 */
@Entity
@Table(name = "user_announcement_views",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "announcement_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnnouncementView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(name = "seen_at", nullable = false)
    private LocalDateTime seenAt;

    @PrePersist
    protected void onCreate() {
        if (seenAt == null) {
            seenAt = LocalDateTime.now();
        }
    }
}


