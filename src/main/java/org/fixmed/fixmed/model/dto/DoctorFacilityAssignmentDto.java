package org.fixmed.fixmed.model.dto;

import lombok.Data;

@Data
public class DoctorFacilityAssignmentDto {
    private Long assignmentId;
    private Long facilityId;
    private String facilityName;
    private String facilityAddress;
    private String roomNumber; 

}