package com.internship.tool.repository;

import com.internship.tool.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatusAndCreatedAtBefore(String status, LocalDateTime cutoffTime);
}