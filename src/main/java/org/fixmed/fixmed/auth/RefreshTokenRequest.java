package org.fixmed.fixmed.auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
