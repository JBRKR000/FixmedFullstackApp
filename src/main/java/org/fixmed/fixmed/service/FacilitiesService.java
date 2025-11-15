package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.model.dto.FacilityReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface FacilitiesService {
    Facilities saveFacility(Facilities facility);
    Optional<Facilities> getFacilityById(Long id);
    Page<Facilities> getAllFacilities(Pageable pageable);
    Facilities updateFacility(Long id, Facilities facility);
    Page<Facilities> searchFacilities( String address, String name, Pageable pageable);
}

