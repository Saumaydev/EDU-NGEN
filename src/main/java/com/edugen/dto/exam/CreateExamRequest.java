package com.edugen.dto.exam;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamRequest {
    @NotBlank(message = "Exam name is required")
    private String examName;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Class section is required")
    private String classSection;

    private String examDescription;

    @NotNull(message = "Total marks is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total marks must be positive")
    private BigDecimal totalMarks;

    private BigDecimal passMarks;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private String examType = "MCQ"; // MCQ, SUBJECTIVE, MIXED

    private Boolean showAnswersAfterExam = true;
    private Boolean showResultsImmediately = true;
    private Boolean allowReviewAfterExam = true;
    private Boolean negativeMarkingEnabled = false;
    private BigDecimal negativeMarksPerQuestion = BigDecimal.ZERO;

    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
}
