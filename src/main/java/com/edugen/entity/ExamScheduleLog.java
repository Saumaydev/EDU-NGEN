package com.edugen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exam_schedule_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamScheduleLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private LocalDateTime scheduledDatetime;

    private LocalDateTime notificationSentAt;

    @Column
    private Integer studentsNotified;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
