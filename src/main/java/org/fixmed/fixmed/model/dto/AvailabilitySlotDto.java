package org.fixmed.fixmed.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AvailabilitySlotDto {
    private Long id;
    private Long assignmentId;
    private String doctorName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String facilityName;
}