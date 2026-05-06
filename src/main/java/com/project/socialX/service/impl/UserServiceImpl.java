package com.project.socialX.service.impl;

import com.project.socialX.domain.User;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.UserService;
import com.project.socialX.service.dto.User.request.ChangePasswordRequest;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User không tồn tại."));

        // 1. Kiểm tra mật khẩu cũ có khớp không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác.");
        }

        // 2. Mã hóa và lưu mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 3. (Tuỳ chọn nhưng khuyên dùng) Xóa tất cả phiên đăng nhập cũ
        // Yêu cầu user đăng nhập lại với mật khẩu mới để đảm bảo an toàn
        refreshTokenService.deleteByUser(user.getId());
    }
}
