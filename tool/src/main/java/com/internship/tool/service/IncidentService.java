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
    private final EmailService emailService;

    @Autowired
    public IncidentService(IncidentRepository incidentRepository, EmailService emailService) {
        this.incidentRepository = incidentRepository;
        this.emailService = emailService;
    }

    @CacheEvict(value = {"incident", "incidentsList"}, allEntries = true)
    public Incident createIncident(Incident incident) {
        if (incident.getTitle() == null || incident.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Incident title cannot be empty");
        }

        if (incident.getStatus() == null || incident.getStatus().trim().isEmpty()) {
            incident.setStatus("OPEN");
        }

        Incident savedIncident = incidentRepository.save(incident);

        // Send email ONLY for new creations
        emailService.sendIncidentCreatedEmail(
                savedIncident.getId(),
                savedIncident.getTitle(),
                savedIncident.getStatus()
        );

        return savedIncident;
    }

    // NEW: Add this method to handle updates (like file uploads)
    // without re-triggering the "New Incident" email
    @CacheEvict(value = {"incident", "incidentsList"}, allEntries = true)
    public Incident updateIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    @Cacheable(value = "incidentsList", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Incident> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable);
    }

    @Cacheable(value = "incident", key = "#id")
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }
}