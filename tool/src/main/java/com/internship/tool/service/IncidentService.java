package com.internship.tool.service;

import com.internship.tool.entity.Incident;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    @Autowired
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public Incident createIncident(Incident incident) {
        // Business logic: Input validation
        if (incident.getTitle() == null || incident.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Incident title cannot be empty");
        }

        // Business logic: Set default status for new incidents
        if (incident.getStatus() == null || incident.getStatus().trim().isEmpty()) {
            incident.setStatus("OPEN");
        }

        return incidentRepository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found with id: " + id));
    }
}