package com.edugen.repository;

import com.edugen.entity.ExamResultsCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamResultsCacheRepository extends JpaRepository<ExamResultsCache, UUID> {
    Optional<ExamResultsCache> findByExamId(UUID examId);
}
