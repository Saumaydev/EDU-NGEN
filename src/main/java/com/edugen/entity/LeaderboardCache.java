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
@Table(name = "leaderboard_cache", indexes = {
    @Index(name = "idx_leaderboard_cache_class_section", columnList = "class_section")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardCache {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID leaderboardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column
    private String classSection;

    @Enumerated(EnumType.STRING)
    private RankingPeriod rankingPeriod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column
    private Integer rankPosition;

    @Column(precision = 5, scale = 2)
    private BigDecimal currentCGPA;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalMarksObtained;

    @Column
    private Integer examCount;

    @Column(nullable = false)
    private LocalDateTime cachedAt;

    @PrePersist
    protected void onCreate() {
        cachedAt = LocalDateTime.now();
    }

    public enum RankingPeriod {
        MONTHLY, QUARTERLY, YEARLY, ALL_TIME
    }
}
