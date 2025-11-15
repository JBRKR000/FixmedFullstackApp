package org.fixmed.fixmed.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {
    private Long patientUserId;
    private Long doctorUserId;
    private String senderType;
    private String content;
}