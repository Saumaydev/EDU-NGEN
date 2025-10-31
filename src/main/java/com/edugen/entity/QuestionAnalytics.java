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
@Table(name = "question_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID analyticsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column
    private Integer totalAttempts;

    @Column
    private Integer correctAttempts;

    @Column
    private Integer incorrectAttempts;

    @Column(precision = 5, scale = 2)
    private BigDecimal accuracyPercentage;

    @Column
    private Integer averageTimeSpentSeconds;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}
