package com.edugen.controller;

import com.edugen.dto.exam.CreateExamRequest;
import com.edugen.dto.exam.CreateQuestionRequest;
import com.edugen.entity.Exam;
import com.edugen.entity.Question;
import com.edugen.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/teacher/exams")
@Tag(name = "Teacher Exams", description = "Teacher exam management APIs")
public class TeacherExamController {

    @Autowired
    private ExamService examService;

    @PostMapping
    @Operation(summary = "Create new exam")
    public ResponseEntity<Exam> createExam(
            Authentication authentication,
            @Valid @RequestBody CreateExamRequest request) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Creating new exam for teacher: {}", teacherId);
        Exam exam = examService.createExam(teacherId, request);
        return new ResponseEntity<>(exam, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all exams created by teacher")
    public ResponseEntity<List<Exam>> getTeacherExams(Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching exams for teacher: {}", teacherId);
        List<Exam> exams = examService.getTeacherExams(teacherId);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/{examId}")
    @Operation(summary = "Get exam details with all questions")
    public ResponseEntity<Exam> getExamDetails(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching exam details: {}", examId);
        Exam exam = examService.getExamDetails(examId, teacherId);
        return new ResponseEntity<>(exam, HttpStatus.OK);
    }

    @PutMapping("/{examId}")
    @Operation(summary = "Update exam (only before publishing)")
    public ResponseEntity<Exam> updateExam(
            @PathVariable UUID examId,
            Authentication authentication,
            @Valid @RequestBody CreateExamRequest request) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Updating exam: {}", examId);
        Exam exam = examService.updateExam(examId, teacherId, request);
        return new ResponseEntity<>(exam, HttpStatus.OK);
    }

    @DeleteMapping("/{examId}")
    @Operation(summary = "Delete exam (only if no student attempts)")
    public ResponseEntity<Map<String, String>> deleteExam(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Deleting exam: {}", examId);
        examService.deleteExam(examId, teacherId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{examId}/publish")
    @Operation(summary = "Publish exam (make available to students)")
    public ResponseEntity<Map<String, Object>> publishExam(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Publishing exam: {}", examId);
        examService.publishExam(examId, teacherId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Exam published successfully");
        response.put("exam_id", examId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{examId}/archive")
    @Operation(summary = "Archive exam (remove from active list)")
    public ResponseEntity<Map<String, String>> archiveExam(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Archiving exam: {}", examId);
        examService.archiveExam(examId, teacherId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Exam archived successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{examId}/questions")
    @Operation(summary = "Add question to exam")
    public ResponseEntity<Question> addQuestion(
            @PathVariable UUID examId,
            Authentication authentication,
            @Valid @RequestBody CreateQuestionRequest request) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Adding question to exam: {}", examId);
        Question question = examService.addQuestion(examId, teacherId, request);
        return new ResponseEntity<>(question, HttpStatus.CREATED);
    }

    @PutMapping("/{examId}/questions/{questionId}")
    @Operation(summary = "Update question")
    public ResponseEntity<Map<String, String>> updateQuestion(
            @PathVariable UUID examId,
            @PathVariable UUID questionId,
            Authentication authentication,
            @Valid @RequestBody CreateQuestionRequest request) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Updating question: {}", questionId);
        examService.updateQuestion(examId, questionId, teacherId, request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Question updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    @Operation(summary = "Delete question")
    public ResponseEntity<Map<String, String>> deleteQuestion(
            @PathVariable UUID examId,
            @PathVariable UUID questionId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Deleting question: {}", questionId);
        examService.deleteQuestion(examId, questionId, teacherId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Question deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{examId}/questions/reorder")
    @Operation(summary = "Reorder questions in exam")
    public ResponseEntity<Map<String, String>> reorderQuestions(
            @PathVariable UUID examId,
            Authentication authentication,
            @RequestBody List<Map<String, Object>> questionOrders) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Reordering questions in exam: {}", examId);
        examService.reorderQuestions(examId, teacherId, questionOrders);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Questions reordered successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
