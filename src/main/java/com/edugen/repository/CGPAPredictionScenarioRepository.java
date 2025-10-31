package com.edugen.repository;

import com.edugen.entity.CGPAPredictionScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CGPAPredictionScenarioRepository extends JpaRepository<CGPAPredictionScenario, UUID> {
    List<CGPAPredictionScenario> findByStudentId(UUID studentId);
}
