package org.fixmed.fixmed.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDto {
    private Long id;
    private Long userId;
    private Long doctorId;
    private Long facilityId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}