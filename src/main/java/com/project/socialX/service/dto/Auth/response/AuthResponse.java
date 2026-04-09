package com.project.socialX.service.dto.Auth.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponse {
    private Long userId;
    private String username;
    private String email;
    private String accessToken;
}
