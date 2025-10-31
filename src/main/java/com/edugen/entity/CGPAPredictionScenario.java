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
@Table(name = "cgpa_prediction_scenarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CGPAPredictionScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID scenarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column
    private String scenarioName;

    @Column(precision = 5, scale = 2)
    private BigDecimal predictedCGPA;

    @Column(precision = 10, scale = 2)
    private BigDecimal inputMarks;

    @Column
    private String inputExamName;

    @Column(nullable = false)
    private LocalDateTime predictionDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
