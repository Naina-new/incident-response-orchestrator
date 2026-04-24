package com.internship.tool.controller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.internship.tool.entity.Incident;
import com.internship.tool.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    // POST /create with @Valid and correct 201 CREATED status
    @PostMapping("/create")
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody Incident incident) {
        Incident createdIncident = incidentService.createIncident(incident);
        return new ResponseEntity<>(createdIncident, HttpStatus.CREATED);
    }

    // GET /all
    // GET /all paginated
    @GetMapping("/all")
    public ResponseEntity<Page<Incident>> getAllIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Incident> incidents = incidentService.getAllIncidents(pageable);
        return new ResponseEntity<>(incidents, HttpStatus.OK);
    }

    // GET /{id} - 404 error is handled by the Exception we made on Day 3
    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        return new ResponseEntity<>(incident, HttpStatus.OK);
    }
}