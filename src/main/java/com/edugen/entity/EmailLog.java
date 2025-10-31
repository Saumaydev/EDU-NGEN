package com.edugen.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_logs", indexes = {
    @Index(name = "idx_email_logs_send_status", columnList = "send_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ParentalReport report;

    @Column(nullable = false)
    private String parentEmail;

    @Column(nullable = false)
    private String emailSubject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String emailBody;

    @Enumerated(EnumType.STRING)
    private SendStatus sendStatus = SendStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime sentAt;

    @Column
    private Integer retryCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum SendStatus {
        PENDING, SENT, FAILED
    }
}
