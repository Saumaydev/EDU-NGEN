package com.edugen.service;

import com.edugen.entity.*;
import com.edugen.exception.BadRequestException;
import com.edugen.exception.ResourceNotFoundException;
import com.edugen.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class CGPAPredictorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private CGPAPredictionScenarioRepository predictionScenarioRepository;

    @Autowired
    private GradeScaleRepository gradeScaleRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    public Map<String, Object> predictCGPA(UUID studentId, String scenarioName, BigDecimal predictedMarks, String examName, BigDecimal totalMarks) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        if (predictedMarks.compareTo(totalMarks) > 0) {
            throw new BadRequestException("Predicted marks cannot exceed total marks");
        }

        // Calculate what the CGPA would be with this new score
        List<ExamAttempt> attempts = examAttemptRepository.findByStudentId(studentId)
                .stream()
                .filter(a -> a.getTotalMarksObtained() != null)
                .toList();

        BigDecimal totalGPA = BigDecimal.ZERO;
        int examCount = 0;

        // Calculate current GPA from existing exams
        for (ExamAttempt attempt : attempts) {
            BigDecimal percentage = attempt.getTotalMarksPercentage();
            if (percentage == null) {
                percentage = attempt.getTotalMarksObtained()
                        .divide(attempt.getExam().getTotalMarks(), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            GradeScale grade = gradeScaleRepository
                    .findBySchoolIdAndMinPercentageLessThanEqualAndMaxPercentageGreaterThanEqual(
                            profile.getSchool().getSchoolId(),
                            percentage,
                            percentage)
                    .orElse(null);

            if (grade != null) {
                totalGPA = totalGPA.add(grade.getGpaPoints());
                examCount++;
            }
        }

        // Add predicted exam score
        BigDecimal predictedPercentage = predictedMarks
                .divide(totalMarks, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        GradeScale predictedGrade = gradeScaleRepository
                .findBySchoolIdAndMinPercentageLessThanEqualAndMaxPercentageGreaterThanEqual(
                        profile.getSchool().getSchoolId(),
                        predictedPercentage,
                        predictedPercentage)
                .orElse(null);

        BigDecimal predictedCGPA = BigDecimal.ZERO;
        if (predictedGrade != null) {
            predictedCGPA = totalGPA.add(predictedGrade.getGpaPoints())
                    .divide(BigDecimal.valueOf(examCount + 1), 2, RoundingMode.HALF_UP);
        }

        // Save prediction scenario
        CGPAPredictionScenario scenario = CGPAPredictionScenario.builder()
                .student(student)
                .scenarioName(scenarioName)
                .predictedCGPA(predictedCGPA)
                .inputMarks(predictedMarks)
                .inputExamName(examName)
                .predictionDate(LocalDateTime.now())
                .build();

        scenario = predictionScenarioRepository.save(scenario);

        Map<String, Object> result = new HashMap<>();
        result.put("scenario_id", scenario.getScenarioId());
        result.put("current_cgpa", profile.getCurrentCGPA());
        result.put("predicted_cgpa", predictedCGPA);
        result.put("cgpa_change", predictedCGPA.subtract(profile.getCurrentCGPA()));
        result.put("predicted_grade", predictedGrade != null ? predictedGrade.getGradeLetter() : "N/A");
        result.put("predicted_percentage", predictedPercentage);

        log.info("CGPA prediction created for student: {}", studentId);
        return result;
    }

    public List<Map<String, Object>> getPredictionScenarios(UUID studentId) {
        List<CGPAPredictionScenario> scenarios = predictionScenarioRepository.findByStudentId(studentId);

        return scenarios.stream()
                .map(s -> Map.of(
                        "scenario_id", s.getScenarioId().toString(),
                        "scenario_name", s.getScenarioName(),
                        "predicted_cgpa", s.getPredictedCGPA(),
                        "input_marks", s.getInputMarks(),
                        "exam_name", s.getInputExamName(),
                        "created_at", s.getCreatedAt().toString()
                ))
                .toList();
    }

    public void deleteScenario(UUID studentId, UUID scenarioId) {
        CGPAPredictionScenario scenario = predictionScenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found"));

        if (!scenario.getStudent().getUserId().equals(studentId)) {
            throw new BadRequestException("Unauthorized access");
        }

        predictionScenarioRepository.delete(scenario);
        log.info("Prediction scenario deleted: {}", scenarioId);
    }
}
