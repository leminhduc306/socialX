package com.project.socialX.service.impl;

import com.project.socialX.domain.Role;
import com.project.socialX.domain.User;
import com.project.socialX.domain.RefreshToken;
import com.project.socialX.service.dto.Auth.request.SignUpRequest;
import com.project.socialX.service.dto.Auth.response.SignUpResponse;
import com.project.socialX.repository.RoleRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.service.AuthService;
import com.project.socialX.web.rest.errors.BadRequestException;
import com.project.socialX.service.dto.Auth.request.SignInRequest;
import com.project.socialX.service.dto.Auth.request.TokenRefreshRequest;
import com.project.socialX.service.dto.Auth.response.AuthResponse;
import com.project.socialX.service.dto.Auth.response.TokenRefreshResponse;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.security.jwt.TokenProvider;
import com.project.socialX.service.impl.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' đã được sử dụng");
        }
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' đã được sử dụng");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name("USER").build()
                ));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRole))
                .build();

        User savedUser = userRepository.save(user);
        return SignUpResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public AuthResponse signIn(SignInRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            throw new BadRequestException("Tài khoản hoặc mật khẩu không đúng");
        }

        String jwt = tokenProvider.createToken(authentication);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        // 1. Kiểm tra Refresh Token còn hợp lệ không
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(request.refreshToken());

        // 2. Lấy User từ Refresh Token
        User user = refreshToken.getUser();

        // 3. Tạo Access Token mới
        String authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(java.util.stream.Collectors.joining(" "));
        String newAccessToken = tokenProvider.createTokenFromEmail(user.getEmail(), authorities);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken()) // Vẫn giữ nguyên refresh token cũ
                .build();
    }

    @Override
    @Transactional
    public void signOut() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        refreshTokenService.deleteByUser(user.getId());
    }
}
