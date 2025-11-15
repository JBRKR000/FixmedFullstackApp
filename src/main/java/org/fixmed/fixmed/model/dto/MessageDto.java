package org.fixmed.fixmed.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private String senderType;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;
}