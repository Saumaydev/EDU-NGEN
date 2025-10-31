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
@Table(name = "exams", indexes = {
    @Index(name = "idx_exams_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_exams_class_section", columnList = "class_section")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID examId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false)
    private String examName;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String classSection;

    @Column(columnDefinition = "TEXT")
    private String examDescription;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalMarks;

    @Column(precision = 10, scale = 2)
    private BigDecimal passMarks;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    private ExamType examType = ExamType.MCQ;

    @Column
    private Boolean showAnswersAfterExam = true;

    @Column
    private Boolean showResultsImmediately = true;

    @Column
    private Boolean allowReviewAfterExam = true;

    @Column
    private Boolean negativeMarkingEnabled = false;

    @Column(precision = 5, scale = 2)
    private BigDecimal negativeMarksPerQuestion = BigDecimal.ZERO;

    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;

    @Column
    private Boolean isPublished = false;

    @Column
    private Boolean isArchived = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ExamType {
        MCQ, SUBJECTIVE, MIXED
    }
}
