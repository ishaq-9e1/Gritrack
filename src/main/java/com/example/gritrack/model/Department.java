package com.example.gritrack.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String officerName;

    @NotBlank
    private String escalationOfficer;

    public Department() {
    }

    public Department(String name, String officerName, String escalationOfficer) {
        this.name = name;
        this.officerName = officerName;
        this.escalationOfficer = escalationOfficer;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    public String getEscalationOfficer() { return escalationOfficer; }
    public void setEscalationOfficer(String escalationOfficer) { this.escalationOfficer = escalationOfficer; }
}
