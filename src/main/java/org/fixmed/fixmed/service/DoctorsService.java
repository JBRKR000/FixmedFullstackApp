package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Patients;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.model.dto.DoctorDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DoctorsService {
    Page<Doctors> getAllDoctors();
    Optional <Doctors> getDoctorById(Long id);
    Doctors saveDoctor(Doctors doctor);
    void deleteDoctor(Long id);

    boolean isDoctorAssignedToFacility(Long id);
    Page<Doctors> searchDoctors(Doctors.Specialization specialization, String city, String name, Users.Gender gender, Pageable pageable);
    Optional<DoctorDetailsDto> getDoctorDetails(Long id);

    Optional<Doctors> getDoctorByUserId(Long userId);
    List<Doctors> getDoctorsWithoutFacility();
    Optional<DoctorDetailsDto> getDoctorByAssignmentId(Long assignmentId);

}
