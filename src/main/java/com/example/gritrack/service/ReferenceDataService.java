package com.example.gritrack.service;

import com.example.gritrack.dto.CategoryRequest;
import com.example.gritrack.dto.DepartmentRequest;
import com.example.gritrack.model.Category;
import com.example.gritrack.model.Department;
import com.example.gritrack.repository.CategoryRepository;
import com.example.gritrack.repository.DepartmentRepository;
import com.example.gritrack.repository.GrievanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceDataService {
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final GrievanceRepository grievanceRepository;

    public ReferenceDataService(CategoryRepository categoryRepository,
                                DepartmentRepository departmentRepository,
                                GrievanceRepository grievanceRepository) {
        this.categoryRepository = categoryRepository;
        this.departmentRepository = departmentRepository;
        this.grievanceRepository = grievanceRepository;
    }

    public List<Category> getCategories() { return categoryRepository.findAll(); }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public Category createCategory(CategoryRequest request) {
        Department department = getDepartment(request.getDepartmentId());
        return categoryRepository.save(new Category(request.getName().trim(), request.getSlaHours(), department));
    }

    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getCategory(id);
        category.setName(request.getName().trim());
        category.setSlaHours(request.getSlaHours());
        category.setDepartment(getDepartment(request.getDepartmentId()));
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategory(id);
        if (grievanceRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Category is used by a grievance and cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    public List<Department> getDepartments() { return departmentRepository.findAll(); }

    public Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
    }

    public Department createDepartment(DepartmentRequest request) {
        return departmentRepository.save(new Department(
                request.getName().trim(), request.getOfficerName().trim(), request.getEscalationOfficer().trim()));
    }

    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department department = getDepartment(id);
        department.setName(request.getName().trim());
        department.setOfficerName(request.getOfficerName().trim());
        department.setEscalationOfficer(request.getEscalationOfficer().trim());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartment(id);
        if (categoryRepository.existsByDepartmentId(id) || grievanceRepository.existsByDepartmentId(id)) {
            throw new IllegalStateException("Department is in use and cannot be deleted");
        }
        departmentRepository.delete(department);
    }
}
