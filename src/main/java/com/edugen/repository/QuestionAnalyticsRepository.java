package com.edugen.repository;

import com.edugen.entity.QuestionAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionAnalyticsRepository extends JpaRepository<QuestionAnalytics, UUID> {
    Optional<QuestionAnalytics> findByQuestionIdAndExamId(UUID questionId, UUID examId);
    List<QuestionAnalytics> findByExamId(UUID examId);
}
