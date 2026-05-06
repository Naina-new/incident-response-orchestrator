package com.internship.tool.repository;

import com.internship.tool.entity.Incident;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = "spring.flyway.enabled=false")
class IncidentRepositoryTest {

    @Autowired
    private IncidentRepository incidentRepository;

    @Test
    void saveAndFindById_ShouldReturnIncident() {
        // Arrange
        Incident incident = new Incident();
        incident.setTitle("DB Test Incident");
        incident.setStatus("OPEN");

        // Act
        Incident savedIncident = incidentRepository.save(incident);
        Optional<Incident> foundIncident = incidentRepository.findById(savedIncident.getId());

        // Assert
        assertThat(foundIncident).isPresent();
        assertThat(foundIncident.get().getTitle()).isEqualTo("DB Test Incident");
    }

    @Test
    void findByNonExistentId_ShouldReturnEmpty() {
        Optional<Incident> foundIncident = incidentRepository.findById(999L);
        assertThat(foundIncident).isEmpty();
    }
}