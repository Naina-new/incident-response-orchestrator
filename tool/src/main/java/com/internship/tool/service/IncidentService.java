package com.internship.tool.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.internship.tool.entity.Incident;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EmailService emailService; // 1. Add EmailService dependency

    // 2. Update constructor to include EmailService
    @Autowired
    public IncidentService(IncidentRepository incidentRepository, EmailService emailService) {
        this.incidentRepository = incidentRepository;
        this.emailService = emailService;
    }

    // Clear ALL caches when a new incident is created so old data isn't shown
    @CacheEvict(value = {"incident", "incidentsList"}, allEntries = true)
    public Incident createIncident(Incident incident) {
        // Business logic: Input validation
        if (incident.getTitle() == null || incident.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Incident title cannot be empty");
        }

        // Business logic: Set default status for new incidents
        if (incident.getStatus() == null || incident.getStatus().trim().isEmpty()) {
            incident.setStatus("OPEN");
        }

        // 3. Save the incident FIRST so the database generates the ID
        Incident savedIncident = incidentRepository.save(incident);

        // 4. FIRE THE EMAIL! (Using the ID, Title, and Status from the saved incident)
        emailService.sendIncidentCreatedEmail(
                savedIncident.getId(),
                savedIncident.getTitle(),
                savedIncident.getStatus()
        );

        // 5. Return the saved incident
        return savedIncident;
    }

    // Cache the paginated list (creates a unique key for each page number/size)
    @Cacheable(value = "incidentsList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Incident> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable);
    }

    // Cache the single ID lookup
    @Cacheable(value = "incident", key = "#id")
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }
}