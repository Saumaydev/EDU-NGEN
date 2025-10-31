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
@Table(name = "exam_results_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResultsCache {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column
    private Integer totalAttempts;

    @Column(precision = 10, scale = 2)
    private BigDecimal averageScore;

    @Column(precision = 10, scale = 2)
    private BigDecimal highestScore;

    @Column(precision = 10, scale = 2)
    private BigDecimal lowestScore;

    @Column
    private Integer passCount;

    @Column
    private Integer failCount;

    @Column(nullable = false)
    private LocalDateTime cachedAt;

    @PrePersist
    protected void onCreate() {
        cachedAt = LocalDateTime.now();
    }
}
