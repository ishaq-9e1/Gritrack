package com.example.gritrack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequest {
    @NotBlank
    private String name;

    @Min(1)
    private int slaHours;

    @NotNull
    private Long departmentId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSlaHours() { return slaHours; }
    public void setSlaHours(int slaHours) { this.slaHours = slaHours; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}
