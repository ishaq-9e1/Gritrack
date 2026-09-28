package com.example.gritrack.repository;

import com.example.gritrack.model.Grievance;
import com.example.gritrack.model.GrievanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findAllByOrderBySubmittedAtDesc();
    List<Grievance> findByEscalatedFalseAndSlaDeadlineBeforeAndStatusNotIn(
            LocalDateTime time, List<GrievanceStatus> statuses);
}
