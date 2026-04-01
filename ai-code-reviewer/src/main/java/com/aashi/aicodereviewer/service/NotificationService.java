package com.aashi.aicodereviewer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${notification.email.to}")
    private String toEmail;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReviewNotification(String repo, int prNumber, String status, String comment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("AI Code Review Completed - PR #" + prNumber);
            message.setText(
                    "Repository: " + repo + "\n" +
                    "PR Number: " + prNumber + "\n" +
                    "Status: " + status + "\n\n" +
                    "Review Details:\n" + comment
            );

            mailSender.send(message);
            log.info("📧 Email notification sent successfully");
        } catch (Exception e) {
            log.error("❌ Failed to send email notification", e);
        }
    }
}