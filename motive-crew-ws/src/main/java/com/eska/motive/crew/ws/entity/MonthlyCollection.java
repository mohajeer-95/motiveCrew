package com.eska.motive.crew.ws.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Monthly collection entity representing collection periods
 * 
 * @author Motive Crew Team
 */
@Entity
@Table(name = "monthly_collections",
       uniqueConstraints = @UniqueConstraint(columnNames = {"year", "month"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "target_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal targetAmount = new BigDecimal("5.00");

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @ManyToOne
    @JoinColumn(name = "locked_by")
    private User lockedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPayment> payments;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

