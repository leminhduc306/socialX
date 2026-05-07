package com.project.socialX.service;

import com.project.socialX.service.dto.Auth.request.SignUpRequest;
import com.project.socialX.service.dto.Auth.response.SignUpResponse;
import com.project.socialX.service.dto.Auth.request.SignInRequest;
import com.project.socialX.service.dto.Auth.request.TokenRefreshRequest;
import com.project.socialX.service.dto.Auth.request.ForgotPasswordRequest;
import com.project.socialX.service.dto.Auth.request.ResetPasswordRequest;
import com.project.socialX.service.dto.Auth.request.SignOutRequest;
import com.project.socialX.service.dto.Auth.response.AuthResponse;
import com.project.socialX.service.dto.Auth.response.TokenRefreshResponse;

public interface AuthService {
    SignUpResponse signUp(SignUpRequest request);
    AuthResponse signIn(SignInRequest request);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    void signOut(SignOutRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
