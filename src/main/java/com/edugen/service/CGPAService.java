package com.edugen.service;

import com.edugen.entity.*;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class CGPAService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private GradeScaleRepository gradeScaleRepository;

    @Autowired
    private CGPAHistoryRepository cgpaHistoryRepository;

    @Autowired
    private StudentSubjectPerformanceRepository studentSubjectPerformanceRepository;

    @Autowired
    private LeaderboardCacheRepository leaderboardCacheRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentResponseRepository studentResponseRepository;

    public void recalculateCGPA(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<ExamAttempt> completedAttempts = examAttemptRepository.findByStudentId(studentId)
                .stream()
                .filter(a -> a.getStatus() == ExamAttempt.AttemptStatus.GRADED ||
                           a.getStatus() == ExamAttempt.AttemptStatus.EVALUATED)
                .filter(a -> a.getTotalMarksObtained() != null)
                .collect(Collectors.toList());

        if (completedAttempts.isEmpty()) {
            profile.setCurrentCGPA(BigDecimal.ZERO);
            studentProfileRepository.save(profile);
            return;
        }

        BigDecimal totalGPA = BigDecimal.ZERO;
        int examCount = 0;

        for (ExamAttempt attempt : completedAttempts) {
            Exam exam = attempt.getExam();
            BigDecimal percentage = attempt.getTotalMarksPercentage();

            if (percentage == null) {
                percentage = attempt.getTotalMarksObtained()
                        .divide(exam.getTotalMarks(), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            // Find corresponding grade
            GradeScale grade = gradeScaleRepository
                    .findBySchoolIdAndMinPercentageLessThanEqualAndMaxPercentageGreaterThanEqual(
                            profile.getSchool().getSchoolId(),
                            percentage,
                            percentage)
                    .orElse(null);

            if (grade != null) {
                totalGPA = totalGPA.add(grade.getGpaPoints());
                examCount++;

                // Update subject performance
                updateSubjectPerformance(studentId, exam.getSubject(), attempt);
            }
        }

        BigDecimal cgpa = totalGPA.divide(BigDecimal.valueOf(examCount), 2, RoundingMode.HALF_UP);
        profile.setCurrentCGPA(cgpa);
        profile.setTotalExamsTaken(examCount);
        studentProfileRepository.save(profile);

        // Save to history
        CGPAHistory history = CGPAHistory.builder()
                .student(student)
                .cgpaValue(cgpa)
                .calculatedFromExams(examCount)
                .calculationDate(LocalDateTime.now())
                .build();
        cgpaHistoryRepository.save(history);

        log.info("CGPA recalculated for student {}: {}", studentId, cgpa);
    }

    private void updateSubjectPerformance(UUID studentId, String subject, ExamAttempt attempt) {
        StudentSubjectPerformance performance = studentSubjectPerformanceRepository
                .findByStudentIdAndSubject(studentId, subject)
                .orElse(StudentSubjectPerformance.builder()
                        .student(userRepository.findById(studentId).orElseThrow())
                        .subject(subject)
                        .build());

        int newTotalExams = (performance.getTotalExams() != null ? performance.getTotalExams() : 0) + 1;
        BigDecimal newTotalMarks = (performance.getTotalMarksObtained() != null ? performance.getTotalMarksObtained() : BigDecimal.ZERO)
                .add(attempt.getTotalMarksObtained());
        BigDecimal newPossibleMarks = (performance.getTotalMarksPossible() != null ? performance.getTotalMarksPossible() : BigDecimal.ZERO)
                .add(attempt.getExam().getTotalMarks());

        BigDecimal avgPercentage = newTotalMarks.divide(newPossibleMarks, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        performance.setTotalExams(newTotalExams);
        performance.setTotalMarksObtained(newTotalMarks);
        performance.setTotalMarksPossible(newPossibleMarks);
        performance.setAveragePercentage(avgPercentage);
        performance.setLastExamDate(attempt.getSubmittedTime());

        // Calculate accuracy rate
        List<StudentResponse> responses = studentResponseRepository.findByAttemptId(attempt.getAttemptId());
        long correctCount = responses.stream()
                .filter(r -> r.getIsCorrect() != null && r.getIsCorrect())
                .count();
        BigDecimal accuracy = BigDecimal.valueOf(correctCount)
                .divide(BigDecimal.valueOf(responses.size()), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        performance.setAccuracyRate(accuracy);

        studentSubjectPerformanceRepository.save(performance);
    }

    public void updateLeaderboard(UUID schoolId, String classSection) {
        List<StudentProfile> students = studentProfileRepository.findByClassSection(classSection)
                .stream()
                .filter(s -> s.getSchool().getSchoolId().equals(schoolId))
                .sorted((s1, s2) -> s2.getCurrentCGPA().compareTo(s1.getCurrentCGPA()))
                .collect(Collectors.toList());

        List<LeaderboardCache> existingCache = leaderboardCacheRepository
                .findBySchoolIdAndClassSectionAndRankingPeriodOrderByRankPosition(
                        schoolId, classSection, LeaderboardCache.RankingPeriod.ALL_TIME);

        // Clear old cache
        leaderboardCacheRepository.deleteAll(existingCache);

        // Create new cache
        int rank = 1;
        for (StudentProfile student : students) {
            LeaderboardCache cache = LeaderboardCache.builder()
                    .school(schoolRepository.findById(schoolId).orElseThrow())
                    .classSection(classSection)
                    .rankingPeriod(LeaderboardCache.RankingPeriod.ALL_TIME)
                    .student(student.getUser())
                    .rankPosition(rank++)
                    .currentCGPA(student.getCurrentCGPA())
                    .examCount(student.getTotalExamsTaken())
                    .build();
            leaderboardCacheRepository.save(cache);
        }

        log.info("Leaderboard updated for class: {}", classSection);
    }

    public Map<String, Object> getStudentPortfolio(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<ExamAttempt> attempts = examAttemptRepository.findByStudentId(studentId)
                .stream()
                .filter(a -> a.getTotalMarksObtained() != null)
                .sorted((a1, a2) -> a2.getSubmittedTime().compareTo(a1.getSubmittedTime()))
                .limit(5)
                .collect(Collectors.toList());

        Map<String, Object> portfolio = new HashMap<>();
        portfolio.put("student_id", studentId);
        portfolio.put("student_name", student.getFullName());
        portfolio.put("class_section", profile.getClassSection());
        portfolio.put("current_cgpa", profile.getCurrentCGPA());
        portfolio.put("total_exams_taken", profile.getTotalExamsTaken());
        portfolio.put("recent_scores", attempts.stream()
                .map(a -> Map.of(
                        "exam_name", a.getExam().getExamName(),
                        "score", a.getTotalMarksObtained(),
                        "percentage", a.getTotalMarksPercentage(),
                        "date", a.getSubmittedTime()
                ))
                .collect(Collectors.toList()));

        List<StudentSubjectPerformance> subjectPerformances = studentSubjectPerformanceRepository
                .findByStudentId(studentId);

        Map<String, BigDecimal> subjectWisePerformance = new HashMap<>();
        for (StudentSubjectPerformance perf : subjectPerformances) {
            subjectWisePerformance.put(perf.getSubject(), perf.getAveragePercentage());
        }
        portfolio.put("subject_wise_performance", subjectWisePerformance);

        return portfolio;
    }

    public Map<String, Object> getPerformanceTrends(UUID studentId) {
        List<CGPAHistory> history = cgpaHistoryRepository
                .findByStudentIdOrderByCalculationDateDesc(studentId)
                .stream()
                .limit(30)
                .collect(Collectors.toList());

        List<Map<String, Object>> trends = history.stream()
                .sorted(Comparator.comparing(CGPAHistory::getCalculationDate))
                .map(h -> Map.of(
                        "date", h.getCalculationDate(),
                        "cgpa", h.getCgpaValue(),
                        "exams_completed", h.getCalculatedFromExams()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("trends", trends);
        return result;
    }

    public Map<String, Object> getSubjectWisePerformance(UUID studentId) {
        List<StudentSubjectPerformance> performances = studentSubjectPerformanceRepository
                .findByStudentId(studentId);

        List<Map<String, Object>> subjects = performances.stream()
                .map(p -> Map.of(
                        "subject", p.getSubject(),
                        "total_exams", p.getTotalExams(),
                        "average_percentage", p.getAveragePercentage(),
                        "accuracy_rate", p.getAccuracyRate(),
                        "trend", p.getTrendImprovement()
                ))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("subjects", subjects);
        return result;
    }
}
