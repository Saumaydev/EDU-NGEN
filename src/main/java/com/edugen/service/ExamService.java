package com.edugen.service;

import com.edugen.dto.exam.CreateExamRequest;
import com.edugen.dto.exam.CreateQuestionRequest;
import com.edugen.entity.*;
import com.edugen.exception.BadRequestException;
import com.edugen.exception.ResourceNotFoundException;
import com.edugen.exception.UnauthorizedException;
import com.edugen.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MCQOptionRepository mcqOptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private StudentResponseRepository studentResponseRepository;

    @Autowired
    private QuestionAnalyticsRepository questionAnalyticsRepository;

    public Exam createExam(UUID teacherId, CreateExamRequest request) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        if (teacher.getUserType() != User.UserType.TEACHER) {
            throw new UnauthorizedException("Only teachers can create exams");
        }

        if (request.getScheduledStartTime() != null && request.getScheduledEndTime() != null) {
            if (request.getScheduledStartTime().isAfter(request.getScheduledEndTime())) {
                throw new BadRequestException("Start time must be before end time");
            }
        }

        Exam exam = Exam.builder()
                .teacher(teacher)
                .examName(request.getExamName())
                .subject(request.getSubject())
                .classSection(request.getClassSection())
                .examDescription(request.getExamDescription())
                .totalMarks(request.getTotalMarks())
                .passMarks(request.getPassMarks())
                .durationMinutes(request.getDurationMinutes())
                .examType(Exam.ExamType.valueOf(request.getExamType().toUpperCase()))
                .showAnswersAfterExam(request.getShowAnswersAfterExam())
                .showResultsImmediately(request.getShowResultsImmediately())
                .allowReviewAfterExam(request.getAllowReviewAfterExam())
                .negativeMarkingEnabled(request.getNegativeMarkingEnabled())
                .negativeMarksPerQuestion(request.getNegativeMarksPerQuestion())
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledEndTime())
                .isPublished(false)
                .isArchived(false)
                .build();

        exam = examRepository.save(exam);
        log.info("Exam created: {} by teacher {}", exam.getExamId(), teacherId);

        return exam;
    }

    public Exam updateExam(UUID examId, UUID teacherId, CreateExamRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can update");
        }

        if (exam.getIsPublished()) {
            throw new BadRequestException("Cannot modify published exam");
        }

        exam.setExamName(request.getExamName());
        exam.setSubject(request.getSubject());
        exam.setClassSection(request.getClassSection());
        exam.setExamDescription(request.getExamDescription());
        exam.setTotalMarks(request.getTotalMarks());
        exam.setPassMarks(request.getPassMarks());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setExamType(Exam.ExamType.valueOf(request.getExamType().toUpperCase()));

        exam = examRepository.save(exam);
        log.info("Exam updated: {}", examId);

        return exam;
    }

    public void publishExam(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can publish");
        }

        long questionCount = questionRepository.countByExamId(examId);
        if (questionCount == 0) {
            throw new BadRequestException("Exam must have at least one question");
        }

        exam.setIsPublished(true);
        examRepository.save(exam);
        log.info("Exam published: {}", examId);
    }

    public void archiveExam(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can archive");
        }

        exam.setIsArchived(true);
        examRepository.save(exam);
        log.info("Exam archived: {}", examId);
    }

    public void deleteExam(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can delete");
        }

        long attemptCount = examAttemptRepository.countByExamId(examId);
        if (attemptCount > 0) {
            throw new BadRequestException("Cannot delete exam that has student attempts");
        }

        examRepository.delete(exam);
        log.info("Exam deleted: {}", examId);
    }

    public List<Exam> getTeacherExams(UUID teacherId) {
        return examRepository.findByTeacherId(teacherId);
    }

    public Exam getExamDetails(UUID examId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Access denied");
        }

        return exam;
    }

    public Question addQuestion(UUID examId, UUID teacherId, CreateQuestionRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can add questions");
        }

        if (exam.getIsPublished()) {
            throw new BadRequestException("Cannot add questions to published exam");
        }

        if (request.getQuestionType().equalsIgnoreCase("MCQ")) {
            if (request.getOptions() == null || request.getOptions().isEmpty()) {
                throw new BadRequestException("MCQ questions must have at least 2 options");
            }

            long correctCount = request.getOptions().stream()
                    .filter(CreateQuestionRequest.CreateMCQOptionRequest::getIsCorrectAnswer)
                    .count();

            if (correctCount != 1) {
                throw new BadRequestException("MCQ questions must have exactly 1 correct answer");
            }
        }

        Question question = Question.builder()
                .exam(exam)
                .questionText(request.getQuestionText())
                .questionType(Question.QuestionType.valueOf(request.getQuestionType().toUpperCase()))
                .marks(request.getMarks())
                .questionOrder(request.getQuestionOrder())
                .build();

        question = questionRepository.save(question);

        if (request.getQuestionType().equalsIgnoreCase("MCQ")) {
            for (CreateQuestionRequest.CreateMCQOptionRequest optionReq : request.getOptions()) {
                MCQOption option = MCQOption.builder()
                        .question(question)
                        .optionText(optionReq.getOptionText())
                        .optionOrder(optionReq.getOptionOrder())
                        .isCorrectAnswer(optionReq.getIsCorrectAnswer())
                        .build();
                mcqOptionRepository.save(option);
            }
        }

        log.info("Question added to exam {}: {}", examId, question.getQuestionId());
        return question;
    }

    public void updateQuestion(UUID examId, UUID questionId, UUID teacherId, CreateQuestionRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can update questions");
        }

        if (exam.getIsPublished()) {
            throw new BadRequestException("Cannot update questions in published exam");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        question.setQuestionText(request.getQuestionText());
        question.setMarks(request.getMarks());
        questionRepository.save(question);

        log.info("Question updated: {}", questionId);
    }

    public void deleteQuestion(UUID examId, UUID questionId, UUID teacherId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can delete questions");
        }

        if (exam.getIsPublished()) {
            throw new BadRequestException("Cannot delete questions from published exam");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        questionRepository.delete(question);
        log.info("Question deleted: {}", questionId);
    }

    public void reorderQuestions(UUID examId, UUID teacherId, List<Map<String, Object>> questionOrders) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTeacher().getUserId().equals(teacherId)) {
            throw new UnauthorizedException("Only exam creator can reorder questions");
        }

        for (Map<String, Object> orderMap : questionOrders) {
            UUID questionId = UUID.fromString((String) orderMap.get("question_id"));
            Integer order = (Integer) orderMap.get("order");

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            question.setQuestionOrder(order);
            questionRepository.save(question);
        }

        log.info("Questions reordered in exam: {}", examId);
    }
}
