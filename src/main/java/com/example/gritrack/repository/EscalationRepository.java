package com.example.gritrack.repository;

import com.example.gritrack.model.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscalationRepository extends JpaRepository<Escalation, Long> {
    List<Escalation> findAllByOrderByEscalatedAtDesc();
    long countByGrievanceId(Long grievanceId);
    void deleteByGrievanceId(Long grievanceId);
}
