package com.example.gritrack.service;

import com.example.gritrack.dto.GrievanceRequest;
import com.example.gritrack.dto.RatingRequest;
import com.example.gritrack.dto.StatusRequest;
import com.example.gritrack.model.Category;
import com.example.gritrack.model.Grievance;
import com.example.gritrack.model.GrievanceStatus;
import com.example.gritrack.repository.CategoryRepository;
import com.example.gritrack.repository.GrievanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GrievanceService {
    private final GrievanceRepository grievanceRepository;
    private final CategoryRepository categoryRepository;

    public GrievanceService(GrievanceRepository grievanceRepository, CategoryRepository categoryRepository) {
        this.grievanceRepository = grievanceRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Grievance> getAll() {
        return grievanceRepository.findAllByOrderBySubmittedAtDesc();
    }

    public Grievance getById(Long id) {
        return grievanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grievance not found"));
    }

    public Grievance create(GrievanceRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        LocalDateTime now = LocalDateTime.now();
        Grievance grievance = new Grievance();
        grievance.setCitizenName(request.getCitizenName().trim());
        grievance.setCitizenEmail(request.getCitizenEmail().trim());
        grievance.setSubject(request.getSubject().trim());
        grievance.setDescription(request.getDescription().trim());
        grievance.setCategory(category);
        grievance.setDepartment(category.getDepartment());
        grievance.setStatus(GrievanceStatus.SUBMITTED);
        grievance.setSubmittedAt(now);
        grievance.setSlaDeadline(now.plusHours(category.getSlaHours()));
        grievance.setEscalated(false);
        return grievanceRepository.save(grievance);
    }

    public Grievance updateStatus(Long id, StatusRequest request) {
        Grievance grievance = getById(id);
        GrievanceStatus current = grievance.getStatus();
        GrievanceStatus next = request.getStatus();
        if (!isValidNext(current, next)) {
            throw new IllegalStateException("Status must follow Submitted -> In progress -> Resolved -> Closed");
        }
        if (next == GrievanceStatus.RESOLVED) {
            if (request.getResolutionNote() == null || request.getResolutionNote().isBlank()) {
                throw new IllegalStateException("Resolution note is required before resolving a grievance");
            }
            grievance.setResolutionNote(request.getResolutionNote().trim());
            grievance.setResolvedAt(LocalDateTime.now());
        }
        if (next == GrievanceStatus.CLOSED) {
            grievance.setClosedAt(LocalDateTime.now());
        }
        grievance.setStatus(next);
        return grievanceRepository.save(grievance);
    }

    public Grievance rate(Long id, RatingRequest request) {
        Grievance grievance = getById(id);
        if (grievance.getStatus() != GrievanceStatus.CLOSED) {
            throw new IllegalStateException("Rating is allowed only after the grievance is closed");
        }
        if (grievance.getRating() != null) {
            throw new IllegalStateException("This grievance has already been rated");
        }
        grievance.setRating(request.getRating());
        return grievanceRepository.save(grievance);
    }

    private boolean isValidNext(GrievanceStatus current, GrievanceStatus next) {
        return (current == GrievanceStatus.SUBMITTED && next == GrievanceStatus.IN_PROGRESS)
                || (current == GrievanceStatus.IN_PROGRESS && next == GrievanceStatus.RESOLVED)
                || (current == GrievanceStatus.RESOLVED && next == GrievanceStatus.CLOSED);
    }
}
