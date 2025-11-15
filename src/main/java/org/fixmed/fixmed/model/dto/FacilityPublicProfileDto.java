package org.fixmed.fixmed.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class FacilityPublicProfileDto {
    private String name;
    private String city;
    private List<String> addresses;
    private String logoUrl;
    private double averageRating;
    private int numberOfReviews;
}