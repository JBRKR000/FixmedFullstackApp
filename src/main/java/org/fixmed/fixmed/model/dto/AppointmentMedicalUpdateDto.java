package org.fixmed.fixmed.model.dto;

import lombok.Data;

@Data
public class AppointmentMedicalUpdateDto {
    private String description;
    private String diagnosis;
    private String recommendations;
    private String prescribedMedications;
}