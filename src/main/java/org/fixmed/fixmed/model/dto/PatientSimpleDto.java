package org.fixmed.fixmed.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientSimpleDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String pesel;
    private String phoneNumber;
}