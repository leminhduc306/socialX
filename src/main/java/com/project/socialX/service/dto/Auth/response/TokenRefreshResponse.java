package com.project.socialX.service.dto.Auth.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}
