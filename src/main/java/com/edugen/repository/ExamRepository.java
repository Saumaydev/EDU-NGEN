package com.edugen.repository;

import com.edugen.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findByTeacherId(UUID teacherId);
    Page<Exam> findByTeacherId(UUID teacherId, Pageable pageable);
    List<Exam> findByClassSection(String classSection);
    List<Exam> findByClassSectionAndIsPublishedTrue(String classSection);
    long countByTeacherId(UUID teacherId);
}
