package com.edugen.service;

import com.edugen.entity.*;
import com.edugen.exception.BadRequestException;
import com.edugen.exception.ResourceNotFoundException;
import com.edugen.exception.UnauthorizedException;
import com.edugen.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TeacherAnalyticsService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private StudentResponseRepository studentResponseRepository;

    @Autowired
    private SubjectiveEvaluationRepository subjectiveEvaluationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnalyticsRepository questionAnalyticsRepository;

    public List<ExamAttempt> getExamAttempts(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Access denied");
        }

        return examAttemptRepository.findByExamId(examId);
    }

    public ExamAttempt getAttemptDetails(UUID examId, UUID attemptId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Access denied");
        }

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        if (!attempt.getExam().getExamId().equals(examId)) {
            throw new BadRequestException("Invalid exam attempt");
        }

        return attempt;
    }

    public void gradeSubjective(UUID examId, UUID attemptId, UUID teacherId, List<Map<String, Object>> grades) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Access denied");
        }

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        BigDecimal totalMarks = BigDecimal.ZERO;
        BigDecimal totalPossible = BigDecimal.ZERO;

        List<StudentResponse> allResponses = studentResponseRepository.findByAttemptId(attemptId);

        for (Map<String, Object> gradeData : grades) {
            UUID responseId = UUID.fromString((String) gradeData.get("response_id"));
            BigDecimal marksGiven = new BigDecimal(gradeData.get("marks_given").toString());
            String feedback = (String) gradeData.get("feedback");

            StudentResponse response = studentResponseRepository.findById(responseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Response not found"));

            // Create or update evaluation
            SubjectiveEvaluation evaluation = subjectiveEvaluationRepository.findByResponseId(responseId)
                    .orElse(SubjectiveEvaluation.builder()
                            .response(response)
                            .teacher(teacher)
                            .build());

            evaluation.setMarksGiven(marksGiven);
            evaluation.setFeedbackText(feedback);
            evaluation.setEvaluationStatus(SubjectiveEvaluation.EvaluationStatus.EVALUATED);
            evaluation.setEvaluatedAt(java.time.LocalDateTime.now());

            subjectiveEvaluationRepository.save(evaluation);
            response.setMarksObtained(marksGiven);
            studentResponseRepository.save(response);

            totalMarks = totalMarks.add(marksGiven);
            totalPossible = totalPossible.add(response.getQuestion().getMarks());
        }

        // Calculate MCQ marks too if mixed exam
        if (exam.getExamType() == Exam.ExamType.MIXED || exam.getExamType() == Exam.ExamType.MCQ) {
            for (StudentResponse response : allResponses) {
                if (response.getResponseType() == StudentResponse.ResponseType.MCQ &&
                    response.getMarksObtained() == null) {
                    if (response.getIsCorrect() != null && response.getIsCorrect()) {
                        response.setMarksObtained(response.getQuestion().getMarks());
                        totalMarks = totalMarks.add(response.getQuestion().getMarks());
                    } else {
                        response.setMarksObtained(BigDecimal.ZERO);
                    }
                    totalPossible = totalPossible.add(response.getQuestion().getMarks());
                    studentResponseRepository.save(response);
                }
            }
        }

        attempt.setTotalMarksObtained(totalMarks);
        BigDecimal percentage = totalMarks.divide(exam.getTotalMarks(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        attempt.setTotalMarksPercentage(percentage);
        attempt.setStatus(ExamAttempt.AttemptStatus.GRADED);

        examAttemptRepository.save(attempt);
        log.info("Subjective grading completed for attempt: {}", attemptId);
    }

    public Map<String, Object> getExamAnalytics(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Access denied");
        }

        List<ExamAttempt> attempts = examAttemptRepository.findByExamId(examId);

        int totalAttempts = attempts.size();
        BigDecimal averageScore = BigDecimal.ZERO;
        BigDecimal highestScore = BigDecimal.ZERO;
        BigDecimal lowestScore = exam.getTotalMarks();
        int passCount = 0;
        int failCount = 0;

        for (ExamAttempt attempt : attempts) {
            if (attempt.getTotalMarksObtained() != null) {
                averageScore = averageScore.add(attempt.getTotalMarksObtained());
                if (attempt.getTotalMarksObtained().compareTo(highestScore) > 0) {
                    highestScore = attempt.getTotalMarksObtained();
                }
                if (attempt.getTotalMarksObtained().compareTo(lowestScore) < 0) {
                    lowestScore = attempt.getTotalMarksObtained();
                }

                if (exam.getPassMarks() != null &&
                    attempt.getTotalMarksObtained().compareTo(exam.getPassMarks()) >= 0) {
                    passCount++;
                } else {
                    failCount++;
                }
            }
        }

        if (totalAttempts > 0) {
            averageScore = averageScore.divide(BigDecimal.valueOf(totalAttempts), 2, RoundingMode.HALF_UP);
        }

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("total_attempts", totalAttempts);
        analytics.put("average_score", averageScore);
        analytics.put("highest_score", highestScore);
        analytics.put("lowest_score", lowestScore);
        analytics.put("pass_count", passCount);
        analytics.put("fail_count", failCount);

        return analytics;
    }

    public List<Map<String, Object>> getLeaderboard(UUID examId, UUID teacherId, int limit) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Access denied");
        }

        List<ExamAttempt> attempts = examAttemptRepository.findByExamId(examId)
                .stream()
                .filter(a -> a.getTotalMarksObtained() != null)
                .sorted((a1, a2) -> a2.getTotalMarksObtained().compareTo(a1.getTotalMarksObtained()))
                .limit(limit)
                .collect(Collectors.toList());

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;

        for (ExamAttempt attempt : attempts) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("rank", rank++);
            entry.put("student_name", attempt.getStudent().getFullName());
            entry.put("score", attempt.getTotalMarksObtained());
            entry.put("percentage", attempt.getTotalMarksPercentage());
            entry.put("time_spent", attempt.getTimeSpentSeconds());
            leaderboard.add(entry);
        }

        return leaderboard;
    }
}
