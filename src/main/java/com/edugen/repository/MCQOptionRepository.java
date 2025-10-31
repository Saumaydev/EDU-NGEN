package com.edugen.repository;

import com.edugen.entity.MCQOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MCQOptionRepository extends JpaRepository<MCQOption, UUID> {
    List<MCQOption> findByQuestionId(UUID questionId);
}
