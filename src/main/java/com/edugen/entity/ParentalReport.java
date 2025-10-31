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
@Table(name = "parental_reports", indexes = {
    @Index(name = "idx_parental_reports_student_id", columnList = "student_id"),
    @Index(name = "idx_parental_reports_exam_id", columnList = "exam_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentalReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @Column(nullable = false)
    private String parentEmail;

    @Column
    private String examName;

    private LocalDateTime examDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal scoreObtained;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalMarks;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column
    private Integer rankInClass;

    @Column(columnDefinition = "TEXT")
    private String teacherFeedback;

    @Column(columnDefinition = "TEXT")
    private String subjectStrengths;

    @Column(columnDefinition = "TEXT")
    private String subjectWeaknesses;

    @Column(columnDefinition = "TEXT")
    private String improvementAreas;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus = ReportStatus.PENDING;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ReportStatus {
        PENDING, SENT, FAILED, BOUNCED
    }
}
