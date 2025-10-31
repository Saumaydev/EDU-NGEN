package com.edugen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leaderboard_improvements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardImprovement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID improvementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column
    private String subject;

    @Column
    private Integer previousRank;

    @Column
    private Integer currentRank;

    @Column(precision = 5, scale = 2)
    private BigDecimal cgpaChange;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
