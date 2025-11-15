package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.FacilityMedicalServices;
import org.fixmed.fixmed.repository.FacilityMedicalServicesRepository;
import org.fixmed.fixmed.service.FacilityMedicalServicesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacilityMedicalServicesServiceImpl implements FacilityMedicalServicesService {

    private final FacilityMedicalServicesRepository repository;

    @Override
    public FacilityMedicalServices save(FacilityMedicalServices service) {
        return repository.save(service);
    }

    @Override
    public Optional<FacilityMedicalServices> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<FacilityMedicalServices> getByFacilityId(Long facilityId) {
        return repository.findByFacilityId(facilityId);
    }

    @Override
    public List<FacilityMedicalServices> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}