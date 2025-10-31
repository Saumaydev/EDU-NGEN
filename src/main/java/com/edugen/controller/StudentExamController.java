package com.edugen.controller;

import com.edugen.dto.exam.SaveResponseRequest;
import com.edugen.entity.Exam;
import com.edugen.entity.ExamAttempt;
import com.edugen.entity.Question;
import com.edugen.entity.StudentResponse;
import com.edugen.service.StudentExamService;
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
@RequestMapping("/student/exams")
@Tag(name = "Student Exams", description = "Student exam taking APIs")
public class StudentExamController {

    @Autowired
    private StudentExamService studentExamService;

    @GetMapping("/upcoming")
    @Operation(summary = "Get list of upcoming exams for student's class")
    public ResponseEntity<List<Exam>> getUpcomingExams(Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching upcoming exams for student: {}", studentId);
        List<Exam> exams = studentExamService.getUpcomingExams(studentId);
        return new ResponseEntity<>(exams, HttpStatus.OK);
    }

    @GetMapping("/{examId}/details")
    @Operation(summary = "Get exam details before attempting")
    public ResponseEntity<Exam> getExamDetails(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching exam details: {}", examId);
        Exam exam = studentExamService.getExamDetails(examId, studentId);
        return new ResponseEntity<>(exam, HttpStatus.OK);
    }

    @PostMapping("/{examId}/start")
    @Operation(summary = "Start exam attempt (enter exam mode)")
    public ResponseEntity<ExamAttempt> startExam(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Starting exam: {}", examId);
        ExamAttempt attempt = studentExamService.startExam(examId, studentId);
        return new ResponseEntity<>(attempt, HttpStatus.CREATED);
    }

    @GetMapping("/{examId}/attempt/{attemptId}/questions")
    @Operation(summary = "Get questions for active exam attempt")
    public ResponseEntity<List<Question>> getExamQuestions(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching questions for exam attempt: {}", attemptId);
        List<Question> questions = studentExamService.getExamQuestions(examId, attemptId, studentId);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @PostMapping("/{examId}/attempt/{attemptId}/save-response")
    @Operation(summary = "Save student's answer (autosave)")
    public ResponseEntity<StudentResponse> saveResponse(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication,
            @Valid @RequestBody SaveResponseRequest request) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Saving response for question: {}", request.getQuestionId());
        StudentResponse response = studentExamService.saveResponse(examId, attemptId, studentId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{examId}/attempt/{attemptId}/submit")
    @Operation(summary = "Submit exam (finalize all responses)")
    public ResponseEntity<ExamAttempt> submitExam(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Submitting exam attempt: {}", attemptId);
        ExamAttempt attempt = studentExamService.submitExam(examId, attemptId, studentId);
        return new ResponseEntity<>(attempt, HttpStatus.OK);
    }

    @PostMapping("/{examId}/attempt/{attemptId}/track-tab-switch")
    @Operation(summary = "Track tab switches during exam")
    public ResponseEntity<Map<String, String>> trackTabSwitch(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Tracking tab switch for attempt: {}", attemptId);
        studentExamService.trackTabSwitch(attemptId, studentId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tab switch recorded");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{examId}/attempt/{attemptId}/results")
    @Operation(summary = "Get exam results after submission")
    public ResponseEntity<ExamAttempt> getExamResults(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching results for exam attempt: {}", attemptId);
        ExamAttempt attempt = studentExamService.getExamResults(examId, attemptId, studentId);
        return new ResponseEntity<>(attempt, HttpStatus.OK);
    }
}
