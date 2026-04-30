package com.internship.tool.controller;

import com.internship.tool.entity.Incident;
import com.internship.tool.service.FileStorageService;
import com.internship.tool.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Important: Added this import

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final FileStorageService fileStorageService;

    @Autowired
    public IncidentController(IncidentService incidentService, FileStorageService fileStorageService) {
        this.incidentService = incidentService;
        this.fileStorageService = fileStorageService;
    }

    // 1. Fixed Create Endpoint (Restored from your previous work)
    @PostMapping("/create")
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody Incident incident) {
        Incident created = incidentService.createIncident(incident);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // 2. Fixed Upload Endpoint
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadAttachment(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Save the file to the physical folder
            String fileName = fileStorageService.storeFile(file);

            // Fetch the existing incident
            Incident incident = incidentService.getIncidentById(id);

            // Set the filename in the database field
            incident.setAttachmentPath(fileName);

            // Use the NEW update method instead of create
            incidentService.updateIncident(incident);

            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            // This is caught by your GlobalExceptionHandler, but we return a 500 here
            // specifically for file IO issues.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + e.getMessage());
        }
    }

    // 3. GET All Paginated
    @GetMapping("/all")
    public ResponseEntity<Page<Incident>> getAllIncidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Incident> incidents = incidentService.getAllIncidents(pageable);
        return new ResponseEntity<>(incidents, HttpStatus.OK);
    }

    // 4. GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Incident incident = incidentService.getIncidentById(id);
        return new ResponseEntity<>(incident, HttpStatus.OK);
    }
}