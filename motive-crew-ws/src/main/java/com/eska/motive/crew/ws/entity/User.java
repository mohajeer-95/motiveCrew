package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User entity representing team members (admins and regular members)
 * 
 * @author Motive Crew Team
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.ToString(exclude = {"payments", "expensesPaid", "eventParticipants", "notifications", "preferences", "createdEvents", "createdExpenses"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private UserRole role = UserRole.MEMBER;

    @Column(length = 100)
    private String position;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPayment> payments;

    @OneToMany(mappedBy = "paidBy", cascade = CascadeType.ALL)
    private List<Expense> expensesPaid;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EventParticipant> eventParticipants;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPreferences preferences;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Event> createdEvents;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Expense> createdExpenses;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (joinedDate == null) {
            joinedDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum UserRole {
        ADMIN, MEMBER
    }
}

