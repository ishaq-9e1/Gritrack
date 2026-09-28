package com.example.gritrack.service;

import com.example.gritrack.model.Category;
import com.example.gritrack.model.Department;
import com.example.gritrack.repository.CategoryRepository;
import com.example.gritrack.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataSetupService implements CommandLineRunner {
    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;

    public DataSetupService(DepartmentRepository departmentRepository, CategoryRepository categoryRepository) {
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0 || categoryRepository.count() > 0) {
            return;
        }
        Department roads = departmentRepository.save(
                new Department("Roads & Infrastructure", "Arun Kumar", "Senior Engineer - Roads"));
        Department water = departmentRepository.save(
                new Department("Water Supply", "Meena S", "Executive Engineer - Water"));
        Department sanitation = departmentRepository.save(
                new Department("Sanitation", "Ravi K", "Health Officer"));
        Department electrical = departmentRepository.save(
                new Department("Street Lighting", "Priya N", "Electrical Supervisor"));

        categoryRepository.save(new Category("Pothole / Road Damage", 48, roads));
        categoryRepository.save(new Category("Water Leakage", 24, water));
        categoryRepository.save(new Category("Garbage / Waste", 12, sanitation));
        categoryRepository.save(new Category("Street Light Fault", 24, electrical));
    }
}
