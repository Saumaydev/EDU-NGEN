package com.edugen.repository;

import com.edugen.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
    List<EmailLog> findBySendStatus(EmailLog.SendStatus status);
    List<EmailLog> findByReportId(UUID reportId);
}
