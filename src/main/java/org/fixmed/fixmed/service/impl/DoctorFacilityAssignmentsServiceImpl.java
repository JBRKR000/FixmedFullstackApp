package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.repository.AvailabilitySlotsRepository;
import org.fixmed.fixmed.repository.DoctorFacilityAssignmentsRepository;
import org.fixmed.fixmed.service.DoctorFacilityAssignmentsService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorFacilityAssignmentsServiceImpl implements DoctorFacilityAssignmentsService {


    private final DoctorFacilityAssignmentsRepository doctorFacilityAssignmentsRepository;
    private final AvailabilitySlotsRepository availabilitySlotsRepository;


    @Override
    public List<DoctorFacilityAssignments> getAllAssignments() {
        return doctorFacilityAssignmentsRepository.findAll();
    }

    @Override
    public Optional<DoctorFacilityAssignments> getAssignmentById(Long id) {
        return doctorFacilityAssignmentsRepository.findById(id);
    }

    @Override
    public DoctorFacilityAssignments createAssignment(DoctorFacilityAssignments assignment) {
        return doctorFacilityAssignmentsRepository.save(assignment);
    }

    @Override
    public DoctorFacilityAssignments updateAssignment(Long id, DoctorFacilityAssignments assignment) {
        Optional<DoctorFacilityAssignments> existingAssignment = doctorFacilityAssignmentsRepository.findById(id);
        if (existingAssignment.isPresent()) {
            DoctorFacilityAssignments updatedAssignment = existingAssignment.get();
            updatedAssignment.setDoctor(assignment.getDoctor());
            updatedAssignment.setFacility(assignment.getFacility());
            updatedAssignment.setSource(assignment.getSource());
            updatedAssignment.setRoomNumber(assignment.getRoomNumber());
            return doctorFacilityAssignmentsRepository.save(updatedAssignment);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
    availabilitySlotsRepository.deleteByAssignment_Id(id);
    doctorFacilityAssignmentsRepository.deleteById(id);
    }

    @Override
    public List<DoctorFacilityAssignments> getAssignments(Long doctorId, Long facilityId) {
        return doctorFacilityAssignmentsRepository.findByDoctor_IdAndFacility_Id(doctorId, facilityId);
    }

    @Override
    public List<DoctorFacilityAssignments> getAssignmentsByDoctor(Long doctorId) {
        return doctorFacilityAssignmentsRepository.findByDoctor_Id(doctorId);
    }

    @Override
    public List<DoctorFacilityAssignments> getAssignmentsByFacility(Long facilityId) {
        return doctorFacilityAssignmentsRepository.findByFacility_Id(facilityId);
    }

    @Override
    public List<DoctorFacilityAssignments> getAssignmentsByFacilityMedicalService(Long facilityMedicalServiceId) {
        return doctorFacilityAssignmentsRepository.findByFacilityMedicalServiceId(facilityMedicalServiceId);
    }
}

