package com.internship.tool.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // @Async means this method runs in a background thread so the user doesn't have to wait for the email to send!
    @Async
    public void sendIncidentCreatedEmail(Long incidentId, String title, String status) {
        try {
            // 1. Prepare the dynamic data for the HTML template
            Context context = new Context();
            context.setVariable("incidentId", incidentId);
            context.setVariable("incidentTitle", title);
            context.setVariable("incidentStatus", status);

            // 2. Process the HTML template
            String htmlBody = templateEngine.process("incident-created", context);

            // 3. Build and send the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("admin@tecsxpert.com"); // Mailtrap catches this anyway!
            helper.setSubject("New Incident Alert: #" + incidentId);
            helper.setText(htmlBody, true); // true = this is HTML, not plain text

            mailSender.send(message);
            System.out.println("Email sent successfully to Mailtrap!");

        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
    @Async
    public void sendOverdueIncidentEmail(Long incidentId, String title) {
        try {
            Context context = new Context();
            context.setVariable("incidentId", incidentId);
            context.setVariable("incidentTitle", title);

            String htmlBody = templateEngine.process("incident-overdue", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("admin@tecsxpert.com");
            helper.setSubject("⚠️ ACTION REQUIRED: Incident #" + incidentId + " is Overdue!");
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("Overdue warning email sent for Incident #" + incidentId);
        } catch (MessagingException e) {
            System.err.println("Failed to send overdue email: " + e.getMessage());
        }
    }
}