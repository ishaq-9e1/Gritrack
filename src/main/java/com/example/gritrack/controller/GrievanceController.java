package com.example.gritrack.controller;

import com.example.gritrack.dto.CategoryRequest;
import com.example.gritrack.dto.DepartmentRequest;
import com.example.gritrack.dto.EscalationRequest;
import com.example.gritrack.dto.GrievanceRequest;
import com.example.gritrack.dto.RatingRequest;
import com.example.gritrack.dto.StatusRequest;
import com.example.gritrack.model.Category;
import com.example.gritrack.model.Department;
import com.example.gritrack.model.Escalation;
import com.example.gritrack.model.Grievance;
import com.example.gritrack.service.EscalationService;
import com.example.gritrack.service.GrievanceService;
import com.example.gritrack.service.ReferenceDataService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final ReferenceDataService referenceDataService;

    public GrievanceController(GrievanceService grievanceService,
                               EscalationService escalationService,
                               ReferenceDataService referenceDataService) {
        this.grievanceService = grievanceService;
        this.escalationService = escalationService;
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/grievances")
    public List<Grievance> getGrievances() { return grievanceService.getAll(); }

    @GetMapping("/grievances/{id}")
    public Grievance getGrievance(@PathVariable Long id) { return grievanceService.getById(id); }

    @PostMapping("/grievances")
    public Grievance createGrievance(@Valid @RequestBody GrievanceRequest request) {
        return grievanceService.create(request);
    }

    @PutMapping("/grievances/{id}/status")
    public Grievance updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return grievanceService.updateStatus(id, request);
    }

    @DeleteMapping("/grievances/{id}")
    public ResponseEntity<Void> deleteGrievance(@PathVariable Long id) {
        grievanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/grievances/{id}/rating")
    public Grievance rate(@PathVariable Long id, @Valid @RequestBody RatingRequest request) {
        return grievanceService.rate(id, request);
    }

    @GetMapping("/categories")
    public List<Category> getCategories() { return referenceDataService.getCategories(); }

    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) { return referenceDataService.getCategory(id); }

    @PostMapping("/categories")
    public Category createCategory(@Valid @RequestBody CategoryRequest request) {
        return referenceDataService.createCategory(request);
    }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return referenceDataService.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        referenceDataService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/departments")
    public List<Department> getDepartments() { return referenceDataService.getDepartments(); }

    @GetMapping("/departments/{id}")
    public Department getDepartment(@PathVariable Long id) { return referenceDataService.getDepartment(id); }

    @PostMapping("/departments")
    public Department createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return referenceDataService.createDepartment(request);
    }

    @PutMapping("/departments/{id}")
    public Department updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return referenceDataService.updateDepartment(id, request);
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        referenceDataService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/escalations")
    public List<Escalation> getEscalations() { return escalationService.getAll(); }

    @GetMapping("/escalations/{id}")
    public Escalation getEscalation(@PathVariable Long id) { return escalationService.getById(id); }

    @PostMapping("/escalations")
    public Escalation createEscalation(@Valid @RequestBody EscalationRequest request) {
        return escalationService.create(request);
    }

    @PutMapping("/escalations/{id}")
    public Escalation updateEscalation(@PathVariable Long id, @Valid @RequestBody EscalationRequest request) {
        return escalationService.update(id, request);
    }

    @DeleteMapping("/escalations/{id}")
    public ResponseEntity<Void> deleteEscalation(@PathVariable Long id) {
        escalationService.delete(id);
        return ResponseEntity.noContent().build();
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
