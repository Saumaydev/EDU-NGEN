package com.edugen.controller;

import com.edugen.entity.ExamAttempt;
import com.edugen.service.TeacherAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Teacher Grading", description = "Teacher exam grading APIs")
public class TeacherGradingController {

    @Autowired
    private TeacherAnalyticsService teacherAnalyticsService;

    @GetMapping("/{examId}/attempts")
    @Operation(summary = "View all student attempts on exam")
    public ResponseEntity<List<ExamAttempt>> getExamAttempts(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching attempts for exam: {}", examId);
        List<ExamAttempt> attempts = teacherAnalyticsService.getExamAttempts(examId, teacherId);
        return new ResponseEntity<>(attempts, HttpStatus.OK);
    }

    @GetMapping("/{examId}/attempts/{attemptId}")
    @Operation(summary = "View single student's attempt details")
    public ResponseEntity<ExamAttempt> getAttemptDetails(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching attempt details: {}", attemptId);
        ExamAttempt attempt = teacherAnalyticsService.getAttemptDetails(examId, attemptId, teacherId);
        return new ResponseEntity<>(attempt, HttpStatus.OK);
    }

    @PostMapping("/{examId}/attempts/{attemptId}/grade-subjective")
    @Operation(summary = "Grade subjective questions")
    public ResponseEntity<Map<String, Object>> gradeSubjective(
            @PathVariable UUID examId,
            @PathVariable UUID attemptId,
            Authentication authentication,
            @RequestBody List<Map<String, Object>> grades) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Grading subjective answers for attempt: {}", attemptId);
        teacherAnalyticsService.gradeSubjective(examId, attemptId, teacherId, grades);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Subjective answers graded successfully");
        response.put("attempt_id", attemptId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{examId}/analytics")
    @Operation(summary = "Get exam analytics")
    public ResponseEntity<Map<String, Object>> getExamAnalytics(
            @PathVariable UUID examId,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching analytics for exam: {}", examId);
        Map<String, Object> analytics = teacherAnalyticsService.getExamAnalytics(examId, teacherId);
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }

    @GetMapping("/{examId}/leaderboard")
    @Operation(summary = "Get exam leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(
            @PathVariable UUID examId,
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication) {
        UUID teacherId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching leaderboard for exam: {}", examId);
        List<Map<String, Object>> leaderboard = teacherAnalyticsService.getLeaderboard(examId, teacherId, limit);
        return new ResponseEntity<>(leaderboard, HttpStatus.OK);
    }
}
