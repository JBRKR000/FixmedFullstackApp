package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.model.dto.FacilityReviewDto;
import org.fixmed.fixmed.model.dto.FacilitySearchResult;
import org.fixmed.fixmed.repository.FacilitiesRepository;
import org.fixmed.fixmed.service.FacilitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacilitiesServiceImpl implements FacilitiesService {
    private final FacilitiesRepository facilitiesRepository;

    @Override
    public Facilities saveFacility(Facilities facility) {
        return facilitiesRepository.save(facility);
    }

    @Override
    public Optional<Facilities> getFacilityById(Long id) {
        return facilitiesRepository.findById(id);
    }

    @Override
    public Page<Facilities> getAllFacilities(Pageable pageable) {
        return facilitiesRepository.findAll(pageable);
    }

    @Override
    public Facilities updateFacility(Long id, Facilities facility) {
        return facilitiesRepository.findById(id)
                .map(existingFacility -> {
                    existingFacility.setName(facility.getName());
                    existingFacility.setAddress(facility.getAddress());
                    existingFacility.setEmail(facility.getEmail());
                    return facilitiesRepository.save(existingFacility);
                }).orElseThrow(() -> new RuntimeException("Facility not found"));
    }
    public static FacilitySearchResult mapToSearchResult(Facilities facility) {
        return FacilitySearchResult.builder()
                .id(facility.getId())
                .name(facility.getName())
                .address(facility.getAddress())
                .build();
    }

    @Override
    public Page<Facilities> searchFacilities(String address, String name,  Pageable pageable) {
        return facilitiesRepository.searchFacilities(address, name, pageable);
    }

   
}
