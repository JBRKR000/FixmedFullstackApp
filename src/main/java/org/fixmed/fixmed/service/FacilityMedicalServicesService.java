package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.FacilityMedicalServices;

import java.util.List;
import java.util.Optional;

public interface FacilityMedicalServicesService {
    FacilityMedicalServices save(FacilityMedicalServices service);
    Optional<FacilityMedicalServices> getById(Long id);
    List<FacilityMedicalServices> getByFacilityId(Long facilityId);
    List<FacilityMedicalServices> getAll();
    void delete(Long id);
}