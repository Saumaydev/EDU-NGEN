package com.edugen.repository;

import com.edugen.entity.StudentSubjectPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentSubjectPerformanceRepository extends JpaRepository<StudentSubjectPerformance, UUID> {
    List<StudentSubjectPerformance> findByStudentId(UUID studentId);
    Optional<StudentSubjectPerformance> findByStudentIdAndSubject(UUID studentId, String subject);
}
