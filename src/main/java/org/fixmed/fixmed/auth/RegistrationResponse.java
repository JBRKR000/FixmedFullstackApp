package org.fixmed.fixmed.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RegistrationResponse {
    private String message;
    private LocalDateTime timestamp;
}