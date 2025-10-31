package com.edugen.controller;

import com.edugen.service.CGPAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/student/portfolio")
@Tag(name = "Student Portfolio", description = "Student portfolio and performance APIs")
public class StudentPortfolioController {

    @Autowired
    private CGPAService cgpaService;

    @GetMapping
    @Operation(summary = "Get student portfolio dashboard")
    public ResponseEntity<Map<String, Object>> getPortfolio(Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching portfolio for student: {}", studentId);
        Map<String, Object> portfolio = cgpaService.getStudentPortfolio(studentId);
        return new ResponseEntity<>(portfolio, HttpStatus.OK);
    }

    @GetMapping("/performance-trends")
    @Operation(summary = "Get performance trends over time")
    public ResponseEntity<Map<String, Object>> getPerformanceTrends(Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching performance trends for student: {}", studentId);
        Map<String, Object> trends = cgpaService.getPerformanceTrends(studentId);
        return new ResponseEntity<>(trends, HttpStatus.OK);
    }

    @GetMapping("/subject-wise")
    @Operation(summary = "Get subject-wise performance breakdown")
    public ResponseEntity<Map<String, Object>> getSubjectWisePerformance(Authentication authentication) {
        UUID studentId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching subject-wise performance for student: {}", studentId);
        Map<String, Object> performance = cgpaService.getSubjectWisePerformance(studentId);
        return new ResponseEntity<>(performance, HttpStatus.OK);
    }
}
