package com.edugen.service;

import com.edugen.entity.*;
import com.edugen.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private ParentalReportRepository parentalReportRepository;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    public void sendParentalReport(UUID attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Exam attempt not found"));

        StudentProfile profile = studentProfileRepository.findByUserId(attempt.getStudent().getUserId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        Exam exam = attempt.getExam();

        // Create parental report
        ParentalReport report = ParentalReport.builder()
                .student(attempt.getStudent())
                .exam(exam)
                .examAttempt(attempt)
                .parentEmail(profile.getParentEmail())
                .examName(exam.getExamName())
                .examDate(attempt.getSubmittedTime())
                .scoreObtained(attempt.getTotalMarksObtained())
                .totalMarks(exam.getTotalMarks())
                .percentage(attempt.getTotalMarksPercentage())
                .reportStatus(ParentalReport.ReportStatus.PENDING)
                .build();

        report = parentalReportRepository.save(report);

        // Generate email body
        String subject = String.format("[%s] Exam Report for %s", appName, attempt.getStudent().getFullName());
        String body = generateParentalReportEmail(
                attempt.getStudent().getFullName(),
                profile.getParentEmail(),
                exam.getExamName(),
                attempt.getTotalMarksObtained(),
                exam.getTotalMarks(),
                attempt.getTotalMarksPercentage());

        // Log email
        EmailLog emailLog = EmailLog.builder()
                .report(report)
                .parentEmail(profile.getParentEmail())
                .emailSubject(subject)
                .emailBody(body)
                .sendStatus(EmailLog.SendStatus.PENDING)
                .build();

        emailLog = emailLogRepository.save(emailLog);

        // Send email with retry logic
        sendEmailWithRetry(emailLog, profile.getParentEmail(), subject, body, 3);

        report.setReportStatus(ParentalReport.ReportStatus.SENT);
        report.setSentAt(LocalDateTime.now());
        parentalReportRepository.save(report);
    }

    private void sendEmailWithRetry(EmailLog emailLog, String to, String subject, String body, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                sendEmail(to, subject, body);
                emailLog.setSendStatus(EmailLog.SendStatus.SENT);
                emailLog.setSentAt(LocalDateTime.now());
                emailLogRepository.save(emailLog);
                log.info("Parental report email sent successfully");
                return;
            } catch (Exception e) {
                retryCount++;
                emailLog.setRetryCount(retryCount);
                if (retryCount >= maxRetries) {
                    emailLog.setSendStatus(EmailLog.SendStatus.FAILED);
                    emailLog.setErrorMessage(e.getMessage());
                    log.error("Failed to send email after {} retries", maxRetries);
                } else {
                    try {
                        // Exponential backoff: 1min, 5min, 15min
                        long delay = Math.min(900000, 60000 * (long) Math.pow(5, retryCount - 1));
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                emailLogRepository.save(emailLog);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *") // Every minute
    public void sendPendingEmails() {
        log.debug("Checking for pending emails...");
        List<EmailLog> pendingEmails = emailLogRepository.findBySendStatus(EmailLog.SendStatus.PENDING);

        for (EmailLog emailLog : pendingEmails) {
            try {
                sendEmail(emailLog.getParentEmail(), emailLog.getEmailSubject(), emailLog.getEmailBody());
                emailLog.setSendStatus(EmailLog.SendStatus.SENT);
                emailLog.setSentAt(LocalDateTime.now());
                emailLogRepository.save(emailLog);
                log.info("Pending email sent to: {}", emailLog.getParentEmail());
            } catch (Exception e) {
                emailLog.setRetryCount(emailLog.getRetryCount() + 1);
                if (emailLog.getRetryCount() >= 3) {
                    emailLog.setSendStatus(EmailLog.SendStatus.FAILED);
                }
                emailLog.setErrorMessage(e.getMessage());
                emailLogRepository.save(emailLog);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void sendExamReminders() {
        log.debug("Sending exam reminders...");
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime tomorrowEnd = tomorrow.plusHours(23).plusMinutes(59);

        List<Exam> upcomingExams = examRepository.findAll().stream()
                .filter(e -> e.getScheduledStartTime() != null &&
                           e.getScheduledStartTime().isAfter(LocalDateTime.now()) &&
                           e.getScheduledStartTime().isBefore(tomorrowEnd))
                .toList();

        for (Exam exam : upcomingExams) {
            List<StudentProfile> students = studentProfileRepository.findByClassSection(exam.getClassSection());
            for (StudentProfile student : students) {
                String subject = String.format("Reminder: %s Exam Tomorrow at %s",
                        exam.getExamName(), exam.getScheduledStartTime().toLocalTime());
                String body = String.format(
                        "Dear %s,\n\n" +
                        "This is a reminder that you have an exam '%s' scheduled for tomorrow.\n" +
                        "Duration: %d minutes\n" +
                        "Total Marks: %s\n\n" +
                        "Please ensure you are logged in 5 minutes before the scheduled time.\n\n" +
                        "Best regards,\n%s",
                        student.getUser().getFullName(),
                        exam.getExamName(),
                        exam.getDurationMinutes(),
                        exam.getTotalMarks(),
                        appName);

                try {
                    sendEmail(student.getUser().getEmail(), subject, body);
                    log.info("Exam reminder sent to: {}", student.getUser().getEmail());
                } catch (Exception e) {
                    log.error("Failed to send reminder to {}: {}", student.getUser().getEmail(), e.getMessage());
                }
            }
        }
    }

    private String generateParentalReportEmail(String studentName, String parentEmail, String examName,
                                               BigDecimal scoreObtained, BigDecimal totalMarks, BigDecimal percentage) {
        return String.format(
                "Dear Parent/Guardian,\n\n" +
                "We are pleased to share your child's exam performance report.\n\n" +
                "=== EXAM DETAILS ===\n" +
                "Student Name: %s\n" +
                "Exam Name: %s\n" +
                "Score: %s out of %s (%.2f%%)\n\n" +
                "=== PERFORMANCE STATUS ===\n" +
                "%s\n\n" +
                "Your child is making progress. Continue to encourage consistent learning and practice.\n\n" +
                "For more details and comprehensive performance analysis, please login to the EDU-NGEN portal.\n\n" +
                "Best regards,\n" +
                "EDU-NGEN Management",
                studentName,
                examName,
                scoreObtained,
                totalMarks,
                percentage != null ? percentage.doubleValue() : 0.0,
                percentage != null && percentage.compareTo(BigDecimal.valueOf(50)) >= 0 ? "PASS ✓" : "NEEDS IMPROVEMENT");
    }
}
