package com.example.gritrack.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EscalationRequest {
    @NotNull
    private Long grievanceId;

    @NotBlank
    private String officerName;

    @NotBlank
    private String reason;

    public Long getGrievanceId() { return grievanceId; }
    public void setGrievanceId(Long grievanceId) { this.grievanceId = grievanceId; }
    public String getOfficerName() { return officerName; }
    public void setOfficerName(String officerName) { this.officerName = officerName; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
