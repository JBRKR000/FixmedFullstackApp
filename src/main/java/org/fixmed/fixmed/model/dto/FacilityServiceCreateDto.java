package org.fixmed.fixmed.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FacilityServiceCreateDto {
    private String name;
    private String description;
    private Long assignmentId;
    private BigDecimal price;
    private Integer durationMinutes;
    private Long facilityId;
}