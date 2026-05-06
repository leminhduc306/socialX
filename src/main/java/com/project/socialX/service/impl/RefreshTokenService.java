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

    /**
     * Tạo Refresh Token mới cho User.
     * Xóa token cũ (nếu có) trước khi tạo mới để tránh rác DB.
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Xóa refresh token cũ nếu có
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshTokenDurationSeconds))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Tìm token trong DB. Kiểm tra còn hạn không.
     */
    public RefreshToken verifyExpiration(String tokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new BadRequestException("Refresh token không tồn tại. Vui lòng đăng nhập lại."));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }

        return token;
    }

    /**
     * Xóa Refresh Token khi User đăng xuất.
     */
    @Transactional
    public void deleteByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }
}
