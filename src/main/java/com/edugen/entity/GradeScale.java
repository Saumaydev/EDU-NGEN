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
@Table(name = "grade_scales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeScale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID scaleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(length = 1)
    private Character gradeLetter;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal minPercentage;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal maxPercentage;

    @Column(precision = 3, scale = 1, nullable = false)
    private BigDecimal gpaPoints;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
