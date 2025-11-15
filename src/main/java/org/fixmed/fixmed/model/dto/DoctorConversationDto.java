package org.fixmed.fixmed.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorConversationDto {
    private Long doctorId;
    private Long userId;
    private String firstName;
    private String lastName;
}