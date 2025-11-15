package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Patients;
import org.fixmed.fixmed.model.dto.PatientSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PatientsService {
    Page<Patients> getAllPatients(Pageable pageable);
    Patients getPatientById(Long id);
    Patients savePatient(Patients patient);
    Patients updatePatient(Long id,Patients patient);
    Optional<Patients> getPatientByUserId(Long userId);
    List<PatientSearchResult> searchPatients(String query, int limit);
}
