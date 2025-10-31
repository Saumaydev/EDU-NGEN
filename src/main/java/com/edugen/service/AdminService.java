package com.edugen.service;

import com.edugen.entity.*;
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
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AdminProfileRepository adminProfileRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    public Map<String, Object> getDashboard(UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        AdminProfile adminProfile = adminProfileRepository.findByUserId(adminId)
                .orElseThrow(() -> new UnauthorizedException("Admin profile not found"));

        UUID schoolId = adminProfile.getSchool() != null ? adminProfile.getSchool().getSchoolId() : null;

        Map<String, Object> dashboard = new HashMap<>();

        // Get school info
        if (schoolId != null) {
            School school = schoolRepository.findById(schoolId).orElseThrow();
            dashboard.put("school_name", school.getSchoolName());
            dashboard.put("school_id", schoolId);
        } else {
            dashboard.put("school_name", "All Schools (Super Admin)");
            dashboard.put("school_id", null);
        }

        // Get statistics
        List<StudentProfile> students = schoolId != null ?
                studentProfileRepository.findBySchoolId(schoolId) :
                studentProfileRepository.findAll();

        List<TeacherProfile> teachers = teacherProfileRepository.findAll().stream()
                .filter(t -> schoolId == null || t.getSchool().getSchoolId().equals(schoolId))
                .collect(Collectors.toList());

        long totalExams = examRepository.findAll().stream()
                .filter(e -> schoolId == null || e.getTeacher().getUserId().equals(schoolId))
                .count();

        long totalAttempts = examAttemptRepository.findAll().stream()
                .filter(a -> a.getStatus() == ExamAttempt.AttemptStatus.GRADED ||
                           a.getStatus() == ExamAttempt.AttemptStatus.EVALUATED)
                .count();

        BigDecimal avgCGPA = students.isEmpty() ? BigDecimal.ZERO :
                students.stream()
                        .map(StudentProfile::getCurrentCGPA)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(students.size()), 2, RoundingMode.HALF_UP);

        dashboard.put("total_students", students.size());
        dashboard.put("total_teachers", teachers.size());
        dashboard.put("total_exams", totalExams);
        dashboard.put("total_attempts", totalAttempts);
        dashboard.put("average_cgpa", avgCGPA);

        return dashboard;
    }

    public List<Map<String, Object>> listUsers(UUID adminId, String userType) {
        AdminProfile adminProfile = adminProfileRepository.findByUserId(adminId)
                .orElseThrow(() -> new UnauthorizedException("Admin profile not found"));

        UUID schoolId = adminProfile.getSchool() != null ? adminProfile.getSchool().getSchoolId() : null;

        List<User> users = userRepository.findAll().stream()
                .filter(u -> userType == null || u.getUserType().toString().equals(userType))
                .collect(Collectors.toList());

        return users.stream()
                .map(u -> Map.of(
                        "user_id", u.getUserId().toString(),
                        "email", u.getEmail(),
                        "full_name", u.getFullName(),
                        "user_type", u.getUserType().toString(),
                        "is_active", u.getIsActive(),
                        "created_at", u.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    public void deactivateUser(UUID adminId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated: {}", userId);
    }

    public void activateUser(UUID adminId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated: {}", userId);
    }

    public Map<String, Object> getOverallPerformance(UUID adminId) {
        List<ExamAttempt> attempts = examAttemptRepository.findAll().stream()
                .filter(a -> a.getTotalMarksObtained() != null)
                .collect(Collectors.toList());

        if (attempts.isEmpty()) {
            return Map.of(
                    "total_attempts", 0,
                    "average_score", BigDecimal.ZERO,
                    "pass_rate", "0%"
            );
        }

        BigDecimal totalMarks = BigDecimal.ZERO;
        int passCount = 0;

        for (ExamAttempt attempt : attempts) {
            totalMarks = totalMarks.add(attempt.getTotalMarksPercentage() != null ?
                    attempt.getTotalMarksPercentage() : BigDecimal.ZERO);
            if (attempt.getTotalMarksPercentage() != null &&
                attempt.getTotalMarksPercentage().compareTo(BigDecimal.valueOf(50)) >= 0) {
                passCount++;
            }
        }

        BigDecimal avgScore = totalMarks.divide(BigDecimal.valueOf(attempts.size()), 2, RoundingMode.HALF_UP);
        BigDecimal passRate = BigDecimal.valueOf(passCount)
                .divide(BigDecimal.valueOf(attempts.size()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        Map<String, Object> result = new HashMap<>();
        result.put("total_attempts", attempts.size());
        result.put("average_score", avgScore);
        result.put("pass_rate", passRate + "%");
        result.put("pass_count", passCount);
        result.put("fail_count", attempts.size() - passCount);

        return result;
    }
}
