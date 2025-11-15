package org.fixmed.fixmed.model.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ServicePriceCreateDto {
    private String name;
    private String description;
    private Long medicalServiceId;
    private Long assignmentId; // ID powiązania lekarz-placówka
    private BigDecimal price;
    private Integer durationTime;
}