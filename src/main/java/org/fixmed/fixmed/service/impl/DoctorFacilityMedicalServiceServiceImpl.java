package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.DoctorFacilityMedicalService;
import org.fixmed.fixmed.repository.DoctorFacilityMedicalServiceRepository;
import org.fixmed.fixmed.service.DoctorFacilityMedicalServiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorFacilityMedicalServiceServiceImpl implements DoctorFacilityMedicalServiceService {

    private final DoctorFacilityMedicalServiceRepository repository;

    @Override
    public DoctorFacilityMedicalService save(DoctorFacilityMedicalService entity) {
        return repository.save(entity);
    }

    @Override
    public List<DoctorFacilityMedicalService> findAll() {
        return repository.findAll();
    }
}