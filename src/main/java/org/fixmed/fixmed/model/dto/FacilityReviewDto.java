package org.fixmed.fixmed.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FacilityReviewDto {
    private Long id;
    private Long patientId;
    private String patientName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}