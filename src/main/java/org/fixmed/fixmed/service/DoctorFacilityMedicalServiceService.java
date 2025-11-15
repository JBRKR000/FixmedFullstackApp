package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.DoctorFacilityMedicalService;

import java.util.List;

public interface DoctorFacilityMedicalServiceService {
    DoctorFacilityMedicalService save(DoctorFacilityMedicalService entity);
    List<DoctorFacilityMedicalService> findAll();
}