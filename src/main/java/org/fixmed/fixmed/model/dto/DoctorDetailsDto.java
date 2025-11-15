package org.fixmed.fixmed.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import org.fixmed.fixmed.model.Doctors;

@Data
@Builder
public class DoctorDetailsDto {
    private Long id;
    private String licenseNumber;
    private String phoneNumber;
    private String city;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> facilities;
    private List<String> services;
    private String specialization;


    public static DoctorDetailsDto fromEntity(Doctors doctor) {
        return DoctorDetailsDto.builder()
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
                .services(doctor.getServices() != null
                        ? doctor.getServices().stream().map(s -> s.getName()).toList()
                        : List.of())
                .facilities(List.of())
                .build();
    }
}
