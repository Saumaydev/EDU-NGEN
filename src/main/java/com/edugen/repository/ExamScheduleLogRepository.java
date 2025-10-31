package com.edugen.repository;

import com.edugen.entity.ExamScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamScheduleLogRepository extends JpaRepository<ExamScheduleLog, UUID> {
    List<ExamScheduleLog> findByExamId(UUID examId);
}
