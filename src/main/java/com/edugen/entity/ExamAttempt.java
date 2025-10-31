package com.edugen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exam_attempts", indexes = {
    @Index(name = "idx_exam_attempts_student_id", columnList = "student_id"),
    @Index(name = "idx_exam_attempts_exam_id", columnList = "exam_id"),
    @Index(name = "idx_exam_attempts_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID attemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column
    private Integer attemptNumber = 1;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private LocalDateTime submittedTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalMarksObtained;

    @Column(precision = 5, scale = 2)
    private BigDecimal totalMarksPercentage;

    @Column
    private Integer timeSpentSeconds;

    @Column
    private Integer tabSwitchCount = 0;

    @Column
    private Boolean isSubmitted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentResponse> responses;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AttemptStatus {
        IN_PROGRESS, SUBMITTED, EVALUATED, GRADED
    }
}
