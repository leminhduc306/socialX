package com.project.socialX.web.rest.impl;
import com.project.socialX.service.dto.Auth.request.SignUpRequest;
import com.project.socialX.service.dto.Auth.request.SignInRequest;
import com.project.socialX.service.dto.Auth.response.SignUpResponse;
import com.project.socialX.service.dto.Auth.response.AuthResponse;
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

    /**
     * POST /api/auth/sign-up
     * Đăng ký tài khoản mới
     */
    @PostMapping("/sign-up")
    public ResponseEntity<Response<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResponse result = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    /**
     * POST /api/auth/sign-in
     * Đăng nhập
     */
    @PostMapping("/sign-in")
    public ResponseEntity<Response<AuthResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        AuthResponse result = authService.signIn(request);
        return ResponseEntity.ok(Response.ok(result));
    }
}
