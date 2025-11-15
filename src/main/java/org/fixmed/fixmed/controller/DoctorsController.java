package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.auth.AuthenticationService;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Users;
import org.fixmed.fixmed.model.dto.DoctorDetailsDto;
import org.fixmed.fixmed.model.dto.DoctorProfileDto;
import org.fixmed.fixmed.model.dto.DoctorSearchResult;
import org.fixmed.fixmed.service.DoctorsService;
import org.fixmed.fixmed.service.impl.DoctorsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorsController {

    private final DoctorsService doctorsService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Doctors> saveDoctor(@RequestBody Doctors doctor) {
        Doctors savedDoctor = doctorsService.saveDoctor(doctor);
        return ResponseEntity.ok(savedDoctor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDetailsDto> getDoctorById(@PathVariable Long id) {
        Optional<DoctorDetailsDto> doctorDetails = doctorsService.getDoctorDetails(id);
        return doctorDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctors> updateDoctor(@PathVariable Long id, @RequestBody Doctors doctor) {
        doctor.setId(id);
        Doctors updatedDoctor = doctorsService.saveDoctor(doctor);
        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        if (doctorsService.isDoctorAssignedToFacility(id)) {
            return ResponseEntity.status(403).build();
        }
        doctorsService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorSearchResult>> searchDoctors(
            @RequestParam(required = false) Doctors.Specialization specialization,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Users.Gender gender,
            Pageable pageable) {
        List<Doctors> doctors = doctorsService.searchDoctors(specialization, city, name, gender, Pageable.unpaged())
                .getContent();
        List<DoctorSearchResult> results = doctors.stream()
                .map(DoctorsServiceImpl::mapToSearchResult)
                .toList();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorProfileDto> getCurrentDoctor(@RequestHeader("Authorization") String authHeader) {
        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        var doctorOpt = doctorsService.getDoctorByUserId(userId);
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctor = doctorOpt.get();
        var user = doctor.getUser();
        DoctorProfileDto dto = new DoctorProfileDto();
        dto.setId(doctor.getId());
        dto.setFirstName(user.getFirst_name());
        dto.setLastName(user.getLast_name());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setPhoneNumber(doctor.getPhone_number());
        dto.setCity(doctor.getCity());
        dto.setSpecialization(doctor.getSpecialization() != null ? doctor.getSpecialization().name() : null);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<DoctorProfileDto> updateCurrentDoctor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DoctorProfileDto updateDto) {
        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        var doctorOpt = doctorsService.getDoctorByUserId(userId);
        if (doctorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var doctor = doctorOpt.get();
        var user = doctor.getUser();
        user.setFirst_name(updateDto.getFirstName());
        user.setLast_name(updateDto.getLastName());
        user.setEmail(updateDto.getEmail());
        user.setGender(updateDto.getGender());
        doctor.setPhone_number(updateDto.getPhoneNumber());
        doctor.setCity(updateDto.getCity());
        if (updateDto.getSpecialization() != null) {
            doctor.setSpecialization(
                    org.fixmed.fixmed.model.Doctors.Specialization.valueOf(updateDto.getSpecialization()));
        }

        doctorsService.saveDoctor(doctor);

        return getCurrentDoctor(authHeader);
    }

    @GetMapping("/getUserIdByDoctorId/{doctorId}")
    public ResponseEntity<Long> getUserIdByDoctorId(@PathVariable Long doctorId) {
        return doctorsService.getDoctorById(doctorId)
                .map(doctor -> ResponseEntity.ok(doctor.getUser().getId()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/without-facility")
    public ResponseEntity<List<DoctorSearchResult>> getDoctorsWithoutFacility() {
        List<Doctors> doctors = doctorsService.getDoctorsWithoutFacility();
        List<DoctorSearchResult> dtos = doctors.stream()
                .map(DoctorsServiceImpl::mapToSearchResult)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/by-assignment/{assignmentId}")
    public ResponseEntity<DoctorDetailsDto> getDoctorByAssignmentId(@PathVariable Long assignmentId) {
        return doctorsService.getDoctorByAssignmentId(assignmentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}