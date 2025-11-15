package org.fixmed.fixmed.service.impl;

import java.util.List;
import java.util.Optional;

import org.fixmed.fixmed.model.Patients;
import org.fixmed.fixmed.model.dto.PatientSearchResult;
import org.fixmed.fixmed.repository.PatientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fixmed.fixmed.service.PatientsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class PatientsServiceImpl implements PatientsService {
    private final PatientsRepository patientsRepository;

    @Autowired
    public PatientsServiceImpl(PatientsRepository patientsRepository) {
        this.patientsRepository = patientsRepository;
    }

    @Override
    public Page<Patients> getAllPatients(Pageable pageable) {
        return patientsRepository.findAll(pageable);
    }

    @Override
    public Patients getPatientById(Long id) {
        return patientsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }

    @Override
    public Patients savePatient(Patients patient) {
        return patientsRepository.save(patient);
    }

    @Override
    public Patients updatePatient(Long id, Patients updatedPatient) {
        Patients existingPatient = patientsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        existingPatient.setBirth_date(updatedPatient.getBirth_date());

        return patientsRepository.save(existingPatient);
    }

    @Override
    public Optional<Patients> getPatientByUserId(Long userId) {
        return patientsRepository.findByUser_Id(userId);
    }

    @Override
    public List<PatientSearchResult> searchPatients(String query, int limit) {
        var patients = patientsRepository.searchByNameOrSurname(query, PageRequest.of(0, limit));
        return patients.stream()
                .map(p -> PatientSearchResult.builder()
                        .id(p.getId())
                        .firstName(p.getUser().getFirst_name())
                        .lastName(p.getUser().getLast_name())
                        .birthDate(p.getBirth_date().toString())
                        .build())
                .toList();
    }
}
