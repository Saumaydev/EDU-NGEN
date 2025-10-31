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
@Table(name = "cgpa_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CGPAHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal cgpaValue;

    @Column
    private Integer calculatedFromExams;

    @Column(nullable = false)
    private LocalDateTime calculationDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
