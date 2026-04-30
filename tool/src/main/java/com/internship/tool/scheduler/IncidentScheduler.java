package com.internship.tool.scheduler;

import com.internship.tool.entity.Incident;
import com.internship.tool.repository.IncidentRepository;
import com.internship.tool.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class IncidentScheduler {

    private final IncidentRepository incidentRepository;
    private final EmailService emailService;

    @Autowired
    public IncidentScheduler(IncidentRepository incidentRepository, EmailService emailService) {
        this.incidentRepository = incidentRepository;
        this.emailService = emailService;
    }

    // Cron expression: "0 * * * * *" means "Run at the start of every single minute"
    @Scheduled(cron = "0 * * * * *")
    public void checkOverdueIncidents() {
        System.out.println("⏰ Scheduler waking up to check for overdue incidents...");

        // For testing, let's say an incident is "overdue" if it's older than 2 minutes!
        // In real life, this might be 24 hours (minusDays(1)) or 4 hours (minusHours(4)).
        LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);

        // Fetch them from the database
        List<Incident> overdueIncidents = incidentRepository.findByStatusAndCreatedAtBefore("OPEN", twoMinutesAgo);

        // Send an email for each one
        for (Incident incident : overdueIncidents) {
            emailService.sendOverdueIncidentEmail(incident.getId(), incident.getTitle());
        }
    }
}