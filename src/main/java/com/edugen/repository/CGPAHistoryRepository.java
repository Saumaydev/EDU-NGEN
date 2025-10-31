package com.edugen.repository;

import com.edugen.entity.CGPAHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CGPAHistoryRepository extends JpaRepository<CGPAHistory, UUID> {
    List<CGPAHistory> findByStudentIdOrderByCalculationDateDesc(UUID studentId);
}
