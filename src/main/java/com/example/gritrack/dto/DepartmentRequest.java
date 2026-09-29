package com.example.gritrack.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String officerName;

    @NotBlank
    private String escalationOfficer;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    public String getEscalationOfficer() { return escalationOfficer; }
    public void setEscalationOfficer(String escalationOfficer) { this.escalationOfficer = escalationOfficer; }
}
