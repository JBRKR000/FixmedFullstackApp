package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.MedicalServices;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.model.dto.DoctorDetailsDto;
import org.fixmed.fixmed.model.dto.DoctorSearchResult;
import org.fixmed.fixmed.repository.DoctorFacilityAssignmentsRepository;
import org.fixmed.fixmed.repository.DoctorsRepository;
import org.fixmed.fixmed.service.DoctorFacilityAssignmentsService;
import org.fixmed.fixmed.service.DoctorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorsServiceImpl implements DoctorsService {

    private final DoctorsRepository doctorsRepository;
    private final DoctorFacilityAssignmentsRepository assignmentsRepository;

    @Override
    public Page<Doctors> getAllDoctors() {
        return doctorsRepository.findAll(Pageable.unpaged());
    }

    @Override
    public Optional<Doctors> getDoctorById(Long id) {
        return doctorsRepository.findById(id);
    }

    @Override
    public Doctors saveDoctor(Doctors doctor) {
        return doctorsRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorsRepository.deleteById(id);
    }

    @Override
    public boolean isDoctorAssignedToFacility(Long id) {
        return doctorsRepository.isDoctorAssignedToFacility(id);
    }

    @Override
    public Page<Doctors> searchDoctors(Doctors.Specialization specialization, String city, String name,
            Users.Gender gender, Pageable pageable) {
        return doctorsRepository.searchDoctors(specialization, city, name, gender, pageable);
    }

    @Override
    public Optional<DoctorDetailsDto> getDoctorDetails(Long id) {
        return doctorsRepository.findById(id).map(doctor -> DoctorDetailsDto.builder()
                .id(doctor.getId())
                .licenseNumber(doctor.getLicense_number())
                .phoneNumber(doctor.getPhone_number())
                .city(doctor.getCity())
                .firstName(doctor.getUser().getFirst_name())
                .lastName(doctor.getUser().getLast_name())
                .email(doctor.getUser().getEmail())
                .specialization(
                        doctor.getSpecialization() != null
                                ? doctor.getSpecialization().name()
                                : null)
                .services(doctor.getServices().stream().map(MedicalServices::getName).toList())
                .build());

    }

    public static DoctorSearchResult mapToSearchResult(Doctors doctor) {
        return DoctorSearchResult.builder()
                .id(doctor.getId())
                .firstName(doctor.getUser().getFirst_name())
                .lastName(doctor.getUser().getLast_name())
                .specialization(
                        doctor.getSpecialization() != null
                                ? doctor.getSpecialization().name()
                                : null)
                .city(doctor.getCity())
                .build();
    }

    @Override
    public Optional<Doctors> getDoctorByUserId(Long userId) {
        return doctorsRepository.findAll().stream()
                .filter(d -> d.getUser().getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<Doctors> getDoctorsWithoutFacility() {
        return doctorsRepository.findDoctorsWithoutFacility();
    }

    @Override
    public Optional<DoctorDetailsDto> getDoctorByAssignmentId(Long assignmentId) {
        return assignmentsRepository.findById(assignmentId)
                .map(assignment -> assignment.getDoctor())
                .map(DoctorDetailsDto::fromEntity);
    }

}