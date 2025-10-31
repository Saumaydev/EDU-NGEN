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
@Table(name = "student_subject_performance", indexes = {
    @Index(name = "idx_student_subject_performance_student_id", columnList = "student_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubjectPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID performanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private String subject;

    @Column
    private Integer totalExams = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalMarksObtained = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalMarksPossible = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal averagePercentage = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal accuracyRate;

    @Column
    private Integer averageTimePerQuestion;

    private LocalDateTime lastExamDate;

    @Column(precision = 5, scale = 2)
    private BigDecimal trendImprovement;

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
}
