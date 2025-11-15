package org.fixmed.fixmed.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilitySearchResult {
    private Long id;
    private String address;
    private String email;
    private String name;
}
