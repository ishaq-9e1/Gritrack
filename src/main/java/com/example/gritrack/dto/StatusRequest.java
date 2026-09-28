package com.example.gritrack.dto;

import com.example.gritrack.model.GrievanceStatus;
import jakarta.validation.constraints.NotNull;

public class StatusRequest {
    @NotNull
    private GrievanceStatus status;

    private String resolutionNote;

    public GrievanceStatus getStatus() { return status; }
    public void setStatus(GrievanceStatus status) { this.status = status; }
    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }
}
