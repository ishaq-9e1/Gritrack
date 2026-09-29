package com.example.gritrack.service;

import com.example.gritrack.dto.EscalationRequest;
import com.example.gritrack.model.Escalation;
import com.example.gritrack.model.Grievance;
import com.example.gritrack.model.GrievanceStatus;
import com.example.gritrack.repository.EscalationRepository;
import com.example.gritrack.repository.GrievanceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EscalationService {
    private final GrievanceRepository grievanceRepository;
    private final EscalationRepository escalationRepository;

    public EscalationService(GrievanceRepository grievanceRepository,
                             EscalationRepository escalationRepository) {
        this.grievanceRepository = grievanceRepository;
        this.escalationRepository = escalationRepository;
    }

    public List<Escalation> getAll() {
        return escalationRepository.findAllByOrderByEscalatedAtDesc();
    }

    public Escalation getById(Long id) {
        return escalationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Escalation not found"));
    }

    @Transactional
    public Escalation create(EscalationRequest request) {
        Grievance grievance = findGrievance(request.getGrievanceId());
        Escalation escalation = new Escalation(
                grievance, request.getOfficerName().trim(), request.getReason().trim(), LocalDateTime.now());
        grievance.setEscalated(true);
        grievanceRepository.save(grievance);
        return escalationRepository.save(escalation);
    }

    public Escalation update(Long id, EscalationRequest request) {
        Escalation escalation = getById(id);
        if (!escalation.getGrievance().getId().equals(request.getGrievanceId())) {
            throw new IllegalStateException("Grievance cannot be changed for an existing escalation");
        }
        escalation.setOfficerName(request.getOfficerName().trim());
        escalation.setReason(request.getReason().trim());
        return escalationRepository.save(escalation);
    }

    @Transactional
    public void delete(Long id) {
        Escalation escalation = getById(id);
        Grievance grievance = escalation.getGrievance();
        long count = escalationRepository.countByGrievanceId(grievance.getId());
        escalationRepository.delete(escalation);
        if (count <= 1) {
            grievance.setEscalated(false);
            grievanceRepository.save(grievance);
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void automaticCheck() {
        checkOverdue();
    }

    @Transactional
    public int checkOverdue() {
        List<GrievanceStatus> finished = List.of(GrievanceStatus.RESOLVED, GrievanceStatus.CLOSED);
        List<Grievance> overdue = grievanceRepository
                .findByEscalatedFalseAndSlaDeadlineBeforeAndStatusNotIn(LocalDateTime.now(), finished);
        for (Grievance grievance : overdue) {
            Escalation escalation = new Escalation(
                    grievance,
                    grievance.getDepartment().getEscalationOfficer(),
                    "SLA deadline exceeded",
                    LocalDateTime.now()
            );
            escalationRepository.save(escalation);
            grievance.setEscalated(true);
            grievanceRepository.save(grievance);
        }
        return overdue.size();
    }

    private Grievance findGrievance(Long id) {
        return grievanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grievance not found"));
    }
}
