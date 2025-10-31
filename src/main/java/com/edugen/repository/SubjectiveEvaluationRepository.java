package com.edugen.repository;

import com.edugen.entity.SubjectiveEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectiveEvaluationRepository extends JpaRepository<SubjectiveEvaluation, UUID> {
    Optional<SubjectiveEvaluation> findByResponseId(UUID responseId);
    List<SubjectiveEvaluation> findByTeacherId(UUID teacherId);
}
