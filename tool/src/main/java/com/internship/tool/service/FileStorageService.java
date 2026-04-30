package com.internship.tool.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String UPLOAD_DIR = "incident_attachments/";

    public FileStorageService() {
        try {
            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Could not create directory: " + e.getMessage());
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        // Sanitize the filename (removes spaces and weird characters)
        String fileName = UUID.randomUUID().toString() + "_" +
                file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.\\-]", "_");

        Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}