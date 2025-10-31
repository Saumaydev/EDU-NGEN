package com.edugen.dto.exam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveResponseRequest {
    private UUID questionId;
    private UUID selectedOptionId; // For MCQ
    private String answerText; // For subjective
}
