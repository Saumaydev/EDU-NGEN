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
@Table(name = "subjective_evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectiveEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private StudentResponse response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal marksGiven;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Enumerated(EnumType.STRING)
    private EvaluationStatus evaluationStatus = EvaluationStatus.PENDING;

    private LocalDateTime evaluatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum EvaluationStatus {
        PENDING, EVALUATED, APPEALED
    }
}
