package com.edugen.controller;

import com.edugen.service.AdminService;
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
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin dashboard and management APIs")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching admin dashboard");
        Map<String, Object> dashboard = adminService.getDashboard(adminId);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<List<Map<String, Object>>> listUsers(
            @RequestParam(required = false) String userType,
            Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Listing users with type: {}", userType);
        List<Map<String, Object>> users = adminService.listUsers(adminId, userType);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users/{userId}/deactivate")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<Map<String, String>> deactivateUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Deactivating user: {}", userId);
        adminService.deactivateUser(adminId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/users/{userId}/activate")
    @Operation(summary = "Activate user")
    public ResponseEntity<Map<String, String>> activateUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Activating user: {}", userId);
        adminService.activateUser(adminId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User activated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reports/overall-performance")
    @Operation(summary = "Get overall school performance report")
    public ResponseEntity<Map<String, Object>> getOverallPerformance(Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("Fetching overall performance report");
        Map<String, Object> performance = adminService.getOverallPerformance(adminId);
        return new ResponseEntity<>(performance, HttpStatus.OK);
    }
}
