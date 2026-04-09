package com.project.socialX.service;

import com.project.socialX.service.dto.Auth.request.SignUpRequest;
import com.project.socialX.service.dto.Auth.response.SignUpResponse;
import com.project.socialX.service.dto.Auth.request.SignInRequest;
import com.project.socialX.service.dto.Auth.response.AuthResponse;

public interface AuthService {

    SignUpResponse signUp(SignUpRequest request);
    AuthResponse signIn(SignInRequest request);
}
