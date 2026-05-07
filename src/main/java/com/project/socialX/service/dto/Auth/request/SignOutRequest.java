package com.project.socialX.service.dto.Auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignOutRequest {
    @NotBlank(message = "Refresh token không được để trống")
    private String refreshToken;
}
