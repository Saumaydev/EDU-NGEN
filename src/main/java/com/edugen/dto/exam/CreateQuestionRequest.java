package com.edugen.dto.exam;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {
    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Question type is required")
    private String questionType; // MCQ or SUBJECTIVE

    @NotNull(message = "Marks is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Marks must be positive")
    private BigDecimal marks;

    private Integer questionOrder;

    // For MCQ questions only
    private List<CreateMCQOptionRequest> options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMCQOptionRequest {
        @NotBlank(message = "Option text is required")
        private String optionText;

        private Integer optionOrder;

        @NotNull(message = "Is correct answer flag is required")
        private Boolean isCorrectAnswer;
    }
}
