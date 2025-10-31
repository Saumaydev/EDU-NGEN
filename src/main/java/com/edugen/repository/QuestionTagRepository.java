package com.edugen.repository;

import com.edugen.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, UUID> {
    List<QuestionTag> findByQuestionId(UUID questionId);
}
