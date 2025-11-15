package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.MedicalServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MedicalServicesService {
    MedicalServices saveMedicalService(MedicalServices medicalService);
    Optional<MedicalServices> getMedicalServiceById(Long id);
    Page<MedicalServices> getAllMedicalServices(Pageable pageable);
    MedicalServices updateMedicalService(Long id, MedicalServices medicalService);
}
