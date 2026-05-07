package com.project.socialX.service.impl;

import com.project.socialX.domain.RefreshToken;
import com.project.socialX.domain.User;
import com.project.socialX.repository.RefreshTokenRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refreshTokenExpiration}") // Mặc đị0 ngày (giây)
    private long refreshTokenDurationSeconds;


    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshTokenDurationSeconds))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    public RefreshToken verifyExpiration(String tokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new BadRequestException("Refresh token không tồn tại. Vui lòng đăng nhập lại."));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        return token;
    }


    @Transactional
    public void deleteByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
