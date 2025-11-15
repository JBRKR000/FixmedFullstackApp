package org.fixmed.fixmed.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorSearchResult {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String city;
}