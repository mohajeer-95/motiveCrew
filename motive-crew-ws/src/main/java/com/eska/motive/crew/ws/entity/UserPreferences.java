package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User preferences entity for app settings
 * 
 * @author Motive Crew Team
 */
@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "notifications_enabled", nullable = false)
    @Builder.Default
    private Boolean notificationsEnabled = true;

    @Column(name = "dark_mode", nullable = false)
    @Builder.Default
    private Boolean darkMode = false;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String language = "en";

    @Column(name = "auto_login", nullable = false)
    @Builder.Default
    private Boolean autoLogin = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_month", nullable = false, length = 20)
    @Builder.Default
    private DefaultMonth defaultMonth = DefaultMonth.CURRENT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DefaultMonth {
        CURRENT, PREVIOUS, CUSTOM
    }
}

