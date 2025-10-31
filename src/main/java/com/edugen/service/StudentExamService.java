package com.edugen.service;

import com.edugen.dto.exam.SaveResponseRequest;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class StudentExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentResponseRepository studentResponseRepository;

    @Autowired
    private MCQOptionRepository mcqOptionRepository;

    @Autowired
    private ExamResultsCacheRepository examResultsCacheRepository;

    public List<Exam> getUpcomingExams(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<Exam> exams = examRepository.findByClassSectionAndIsPublishedTrue(profile.getClassSection());

        return exams.stream()
                .filter(exam -> exam.getScheduledStartTime() != null &&
                        exam.getScheduledStartTime().isAfter(LocalDateTime.now()))
                .filter(exam -> !exam.getIsArchived())
                .collect(Collectors.toList());
    }

    public Exam getExamDetails(UUID examId, UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getClassSection().equals(profile.getClassSection())) {
            throw new UnauthorizedException("Student not in correct class for this exam");
        }

        if (!exam.getIsPublished()) {
            throw new UnauthorizedException("Exam not yet published");
        }

        LocalDateTime now = LocalDateTime.now();
        if (exam.getScheduledStartTime() != null && now.isBefore(exam.getScheduledStartTime())) {
            throw new BadRequestException("Exam has not started yet");
        }

        if (exam.getScheduledEndTime() != null && now.isAfter(exam.getScheduledEndTime())) {
            throw new BadRequestException("Exam has ended");
        }

        return exam;
    }

    public ExamAttempt startExam(UUID examId, UUID studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        if (!exam.getClassSection().equals(profile.getClassSection())) {
            throw new UnauthorizedException("Student not in correct class for this exam");
        }

        if (!exam.getIsPublished()) {
            throw new UnauthorizedException("Exam not yet published");
        }

        LocalDateTime now = LocalDateTime.now();
        if (exam.getScheduledStartTime() != null && now.isBefore(exam.getScheduledStartTime())) {
            throw new BadRequestException("Exam has not started yet");
        }

        if (exam.getScheduledEndTime() != null && now.isAfter(exam.getScheduledEndTime())) {
            throw new BadRequestException("Exam has ended");
        }

        ExamAttempt attempt = ExamAttempt.builder()
                .student(student)
                .exam(exam)
                .attemptNumber(1)
                .startTime(now)
                .status(ExamAttempt.AttemptStatus.IN_PROGRESS)
                .tabSwitchCount(0)
                .isSubmitted(false)
                .build();

        attempt = examAttemptRepository.save(attempt);
        log.info("Exam attempt started: {} by student {}", attempt.getAttemptId(), studentId);

        return attempt;
    }

    public List<Question> getExamQuestions(UUID examId, UUID attemptId, UUID studentId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        if (!attempt.getStudent().getUserId().equals(studentId)) {
            throw new UnauthorizedException("Access denied");
        }

        if (!attempt.getExam().getExamId().equals(examId)) {
            throw new BadRequestException("Invalid exam attempt");
        }

        if (!ExamAttempt.AttemptStatus.IN_PROGRESS.equals(attempt.getStatus())) {
            throw new BadRequestException("Exam attempt is not in progress");
        }

        return questionRepository.findByExamId(examId);
    }

    public StudentResponse saveResponse(UUID examId, UUID attemptId, UUID studentId, SaveResponseRequest request) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        if (!attempt.getStudent().getUserId().equals(studentId)) {
            throw new UnauthorizedException("Access denied");
        }

        if (!ExamAttempt.AttemptStatus.IN_PROGRESS.equals(attempt.getStatus())) {
            throw new BadRequestException("Exam attempt is not in progress");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        StudentResponse response = studentResponseRepository
                .findByAttemptIdAndQuestionId(attemptId, request.getQuestionId())
                .orElse(StudentResponse.builder()
                        .attempt(attempt)
                        .question(question)
                        .build());

        if (question.getQuestionType() == Question.QuestionType.MCQ) {
            if (request.getSelectedOptionId() == null) {
                response.setSelectedOption(null);
            } else {
                MCQOption option = mcqOptionRepository.findById(request.getSelectedOptionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
                response.setSelectedOption(option);
                response.setIsCorrect(option.getIsCorrectAnswer());
            }
            response.setResponseType(StudentResponse.ResponseType.MCQ);
        } else {
            response.setSubjectiveAnswer(request.getAnswerText());
            response.setResponseType(StudentResponse.ResponseType.SUBJECTIVE);
        }

        response = studentResponseRepository.save(response);
        log.info("Response saved for question: {}", question.getQuestionId());

        return response;
    }

    public ExamAttempt submitExam(UUID examId, UUID attemptId, UUID studentId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        if (!attempt.getStudent().getUserId().equals(studentId)) {
            throw new UnauthorizedException("Access denied");
        }

        if (!ExamAttempt.AttemptStatus.IN_PROGRESS.equals(attempt.getStatus())) {
            throw new BadRequestException("Exam attempt is not in progress");
        }

        Exam exam = attempt.getExam();
        LocalDateTime now = LocalDateTime.now();

        if (exam.getScheduledEndTime() != null && now.isAfter(exam.getScheduledEndTime())) {
            // Grace period of 5 seconds
            if (now.isAfter(exam.getScheduledEndTime().plusSeconds(5))) {
                throw new BadRequestException("Exam submission deadline exceeded");
            }
        }

        attempt.setStatus(ExamAttempt.AttemptStatus.SUBMITTED);
        attempt.setSubmittedTime(now);
        attempt.setIsSubmitted(true);

        // Calculate time spent
        long secondsSpent = java.time.temporal.ChronoUnit.SECONDS.between(attempt.getStartTime(), now);
        attempt.setTimeSpentSeconds((int) secondsSpent);

        // For MCQ exams, auto-calculate scores
        if (exam.getExamType() == Exam.ExamType.MCQ) {
            BigDecimal totalMarks = BigDecimal.ZERO;
            List<StudentResponse> responses = studentResponseRepository.findByAttemptId(attemptId);

            for (StudentResponse response : responses) {
                Question question = response.getQuestion();
                if (response.getIsCorrect() != null && response.getIsCorrect()) {
                    totalMarks = totalMarks.add(question.getMarks());
                } else if (exam.getNegativeMarkingEnabled() && response.getIsCorrect() != null && !response.getIsCorrect()) {
                    totalMarks = totalMarks.subtract(exam.getNegativeMarksPerQuestion());
                }
            }

            attempt.setTotalMarksObtained(totalMarks);
            BigDecimal percentage = totalMarks.divide(exam.getTotalMarks(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            attempt.setTotalMarksPercentage(percentage);
            attempt.setStatus(ExamAttempt.AttemptStatus.EVALUATED);
        } else if (exam.getExamType() == Exam.ExamType.MIXED) {
            // For mixed exams, calculate MCQ scores, wait for subjective grading
            BigDecimal mcqMarks = BigDecimal.ZERO;
            List<StudentResponse> responses = studentResponseRepository.findByAttemptId(attemptId);

            for (StudentResponse response : responses) {
                if (response.getResponseType() == StudentResponse.ResponseType.MCQ &&
                    response.getIsCorrect() != null && response.getIsCorrect()) {
                    mcqMarks = mcqMarks.add(response.getQuestion().getMarks());
                }
            }
            attempt.setTotalMarksObtained(mcqMarks);
        } else {
            // For subjective exams, wait for teacher grading
            attempt.setStatus(ExamAttempt.AttemptStatus.SUBMITTED);
        }

        attempt = examAttemptRepository.save(attempt);
        log.info("Exam submitted: {} by student {}", attemptId, studentId);

        return attempt;
    }

    public void trackTabSwitch(UUID attemptId, UUID studentId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        if (!attempt.getStudent().getUserId().equals(studentId)) {
            throw new UnauthorizedException("Access denied");
        }

        attempt.setTabSwitchCount(attempt.getTabSwitchCount() + 1);
        examAttemptRepository.save(attempt);
        log.info("Tab switch tracked: {} (count: {})", attemptId, attempt.getTabSwitchCount());
    }

    public ExamAttempt getExamResults(UUID examId, UUID attemptId, UUID studentId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam attempt not found"));

        if (!attempt.getStudent().getUserId().equals(studentId)) {
            throw new UnauthorizedException("Access denied");
        }

        if (!attempt.getExam().getExamId().equals(examId)) {
            throw new BadRequestException("Invalid exam attempt");
        }

        if (!attempt.getIsSubmitted()) {
            throw new BadRequestException("Exam not yet submitted");
        }

        Exam exam = attempt.getExam();
        if (!exam.getShowResultsImmediately() &&
            attempt.getStatus() == ExamAttempt.AttemptStatus.SUBMITTED) {
            throw new BadRequestException("Results not yet available - pending teacher evaluation");
        }

        return attempt;
    }
}
