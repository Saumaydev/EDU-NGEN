package com.edugen.repository;

import com.edugen.entity.StudentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentResponseRepository extends JpaRepository<StudentResponse, UUID> {
    List<StudentResponse> findByAttemptId(UUID attemptId);
    Optional<StudentResponse> findByAttemptIdAndQuestionId(UUID attemptId, UUID questionId);
    List<StudentResponse> findByQuestionId(UUID questionId);
}
