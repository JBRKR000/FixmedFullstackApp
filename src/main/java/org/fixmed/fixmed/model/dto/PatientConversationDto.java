package org.fixmed.fixmed.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientConversationDto {
    private Long patientId;
    private Long userId;
    private String firstName;
    private String lastName;
}