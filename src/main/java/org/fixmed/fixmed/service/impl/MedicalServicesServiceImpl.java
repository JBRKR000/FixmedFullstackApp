package org.fixmed.fixmed.service.impl;

import org.fixmed.fixmed.model.MedicalServices;
import org.fixmed.fixmed.repository.MedicalServicesRepository;
import org.fixmed.fixmed.service.MedicalServicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicalServicesServiceImpl implements MedicalServicesService {
    private final MedicalServicesRepository repository;

    @Autowired
    public MedicalServicesServiceImpl(MedicalServicesRepository repository) {
        this.repository = repository;
    }

    @Override
    public MedicalServices saveMedicalService(MedicalServices medicalService) {
        return repository.save(medicalService);
    }

    @Override
    public Optional<MedicalServices> getMedicalServiceById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<MedicalServices> getAllMedicalServices(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public MedicalServices updateMedicalService(Long id, MedicalServices medicalService) {
        return repository.findById(id)
                .map(existingService -> {
                    existingService.setName(medicalService.getName());
                    existingService.setDescription(medicalService .getDescription());
                    return repository.save(existingService);
                }).orElseThrow(() -> new RuntimeException("Medical Service not found"));
    }
}
