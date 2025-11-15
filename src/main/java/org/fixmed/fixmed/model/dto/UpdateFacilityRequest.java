package org.fixmed.fixmed.model.dto;
import lombok.Data;

@Data
public class UpdateFacilityRequest {
    private String name;
    private String email;
    private String address;
}