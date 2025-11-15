package org.fixmed.fixmed.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateOrUpdateServiceRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int durationMinutes;
    private Long assignmentId;
    private Long doctorId;
}