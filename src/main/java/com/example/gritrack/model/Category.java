package com.example.gritrack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Min(1)
    private int slaHours;

    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    public Category() {
    }

    public Category(String name, int slaHours, Department department) {
        this.name = name;
        this.slaHours = slaHours;
        this.department = department;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSlaHours() { return slaHours; }
    public void setSlaHours(int slaHours) { this.slaHours = slaHours; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
