package com.edugen.repository;

import com.edugen.entity.LeaderboardImprovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaderboardImprovementRepository extends JpaRepository<LeaderboardImprovement, UUID> {
    List<LeaderboardImprovement> findByStudentId(UUID studentId);
}
