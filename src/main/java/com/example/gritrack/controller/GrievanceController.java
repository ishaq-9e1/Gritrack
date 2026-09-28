package com.example.gritrack.controller;

import com.example.gritrack.dto.GrievanceRequest;
import com.example.gritrack.dto.RatingRequest;
import com.example.gritrack.dto.StatusRequest;
import com.example.gritrack.model.Category;
import com.example.gritrack.model.Department;
import com.example.gritrack.model.Escalation;
import com.example.gritrack.model.Grievance;
import com.example.gritrack.repository.CategoryRepository;
import com.example.gritrack.repository.DepartmentRepository;
import com.example.gritrack.service.EscalationService;
import com.example.gritrack.service.GrievanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GrievanceController {
    private final GrievanceService grievanceService;
    private final EscalationService escalationService;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;

    public GrievanceController(GrievanceService grievanceService,
                               EscalationService escalationService,
                               CategoryRepository categoryRepository,
                               DepartmentRepository departmentRepository) {
        this.grievanceService = grievanceService;
        this.escalationService = escalationService;
        this.categoryRepository = categoryRepository;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/grievances")
    public List<Grievance> getGrievances() {
        return grievanceService.getAll();
    }

    @PostMapping("/grievances")
    public Grievance createGrievance(@Valid @RequestBody GrievanceRequest request) {
        return grievanceService.create(request);
    }

    @PutMapping("/grievances/{id}/status")
    public Grievance updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return grievanceService.updateStatus(id, request);
    }

    @PostMapping("/grievances/{id}/rating")
    public Grievance rate(@PathVariable Long id, @Valid @RequestBody RatingRequest request) {
        return grievanceService.rate(id, request);
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/departments")
    public List<Department> getDepartments() {
        return departmentRepository.findAll();
    }

    @GetMapping("/escalations")
    public List<Escalation> getEscalations() {
        return escalationService.getAll();
    }

    @PostMapping("/escalations/check")
    public Map<String, Integer> checkEscalations() {
        return Map.of("newEscalations", escalationService.checkOverdue());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBusinessError(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }
}
