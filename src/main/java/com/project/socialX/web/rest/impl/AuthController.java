package com.project.socialX.web.rest.impl;

import com.project.socialX.service.dto.Auth.request.SignUpRequest;
import com.project.socialX.service.dto.Auth.request.SignInRequest;
import com.project.socialX.service.dto.Auth.request.TokenRefreshRequest;
import com.project.socialX.service.dto.Auth.request.ForgotPasswordRequest;
import com.project.socialX.service.dto.Auth.request.ResetPasswordRequest;
import com.project.socialX.service.dto.Auth.request.SignOutRequest;
import com.project.socialX.service.dto.Auth.response.SignUpResponse;
import com.project.socialX.service.dto.Auth.response.AuthResponse;
import com.project.socialX.service.dto.Auth.response.TokenRefreshResponse;
import com.project.socialX.dto.response.Response;
import com.project.socialX.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<Response<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(authService.signUp(request)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Response<AuthResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(Response.ok(authService.signIn(request)));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Response<TokenRefreshResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(Response.ok(authService.refreshToken(request)));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Response<Void>> signOut(@Valid @RequestBody SignOutRequest request) {
        authService.signOut(request);
        return ResponseEntity.ok(Response.ok(null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(Response.ok(null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Response.ok(null));
    }
}
