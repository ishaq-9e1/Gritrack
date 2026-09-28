package com.example.gritrack.service;

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
            String officer = grievance.getDepartment().getEscalationOfficer();
            Escalation escalation = new Escalation(
                    grievance,
                    officer,
                    "SLA deadline exceeded",
                    LocalDateTime.now()
            );
            escalationRepository.save(escalation);
            grievance.setEscalated(true);
            grievanceRepository.save(grievance);
        }
        return overdue.size();
    }
}
