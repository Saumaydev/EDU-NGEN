package com.edugen.repository;

import com.edugen.entity.ParentalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParentalReportRepository extends JpaRepository<ParentalReport, UUID> {
    List<ParentalReport> findByStudentId(UUID studentId);
    List<ParentalReport> findByExamId(UUID examId);
    List<ParentalReport> findByReportStatus(ParentalReport.ReportStatus status);
}
