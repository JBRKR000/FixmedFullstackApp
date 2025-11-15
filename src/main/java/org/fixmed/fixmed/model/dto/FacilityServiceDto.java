package org.fixmed.fixmed.model.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FacilityServiceDto {
    private String serviceName;
    private BigDecimal price;
    private int durationMinutes;
}