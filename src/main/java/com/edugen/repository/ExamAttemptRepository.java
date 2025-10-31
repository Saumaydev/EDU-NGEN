package com.edugen.repository;

import com.edugen.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {
    List<ExamAttempt> findByStudentId(UUID studentId);
    List<ExamAttempt> findByExamId(UUID examId);
    Optional<ExamAttempt> findByStudentIdAndExamIdAndAttemptNumber(UUID studentId, UUID examId, Integer attemptNumber);
    long countByStudentIdAndExamId(UUID studentId, UUID examId);
    List<ExamAttempt> findByExamIdAndStatus(UUID examId, ExamAttempt.AttemptStatus status);
}
