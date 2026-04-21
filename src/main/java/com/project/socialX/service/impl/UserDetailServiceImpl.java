package com.project.socialX.service.impl;

import com.project.socialX.domain.User;
import com.project.socialX.domain.UserDetail;
import com.project.socialX.intergration.MinioChannel;
import com.project.socialX.repository.UserDetailRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.UserDetailService;
import com.project.socialX.service.dto.User.UserDetailRequest;
import com.project.socialX.service.dto.User.UserDetailResponse;
import com.project.socialX.service.mapper.UserDetailMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final UserRepository userRepository;
    private final UserDetailMapper userDetailMapper;
    private final MinioChannel minioChannel;

    // ─── helpers ──────────────────────────────────────────────────────────────

    /** Lấy User entity từ email trong Security Context. */
    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    /** Lấy User entity theo id — dùng cho admin API. */
    private User resolveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));
    }

    /** Upload avatar lên MinIO nếu file hợp lệ, trả về public URL. Trả null nếu không có file. */
    private String uploadAvatar(MultipartFile avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            return minioChannel.upload(avatar);
        }
        return null;
    }

    // ─── My Profile ───────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getMyUserDetail() {
        User user = currentUser();
        UserDetail detail = userDetailRepository.findById(user.getId())
                .orElseThrow(() -> new BadRequestException("User detail not found"));
        return userDetailMapper.toResponse(detail);
    }

    @Override
    @Transactional
    public UserDetailResponse createMyUserDetail(UserDetailRequest request, MultipartFile avatar) {
        User user = currentUser();
        Long userId = user.getId();

        if (userDetailRepository.existsById(userId)) {
            throw new BadRequestException("User detail already exists — use update instead");
        }

        UserDetail detail = userDetailMapper.toEntity(request);
        detail.setUser(user);

        String avatarUrl = uploadAvatar(avatar);
        if (avatarUrl != null) detail.setAvatarUrl(avatarUrl);

        return userDetailMapper.toResponse(userDetailRepository.save(detail));
    }

    @Override
    @Transactional
    public UserDetailResponse updateMyUserDetail(UserDetailRequest request, MultipartFile avatar) {
        User user = currentUser();
        Long userId = user.getId();

        UserDetail detail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User detail not found — create it first"));

        userDetailMapper.partialUpdate(request, detail);

        String avatarUrl = uploadAvatar(avatar);
        if (avatarUrl != null) detail.setAvatarUrl(avatarUrl);

        return userDetailMapper.toResponse(userDetailRepository.save(detail));
    }

    // ─── Admin ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        resolveUser(userId);
        UserDetail detail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User detail not found for userId: " + userId));
        return userDetailMapper.toResponse(detail);
    }

    @Override
    @Transactional
    public UserDetailResponse createUserDetail(Long userId, UserDetailRequest request, MultipartFile avatar) {
        User user = resolveUser(userId);

        if (userDetailRepository.existsById(userId)) {
            throw new BadRequestException("User detail already exists for userId: " + userId);
        }

        UserDetail detail = userDetailMapper.toEntity(request);
        detail.setId(userId);
        detail.setUser(user);

        String avatarUrl = uploadAvatar(avatar);
        if (avatarUrl != null) detail.setAvatarUrl(avatarUrl);

        return userDetailMapper.toResponse(userDetailRepository.save(detail));
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserDetail(Long userId, UserDetailRequest request, MultipartFile avatar) {
        resolveUser(userId);

        UserDetail detail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User detail not found for userId: " + userId));

        userDetailMapper.partialUpdate(request, detail);

        String avatarUrl = uploadAvatar(avatar);
        if (avatarUrl != null) detail.setAvatarUrl(avatarUrl);

        return userDetailMapper.toResponse(userDetailRepository.save(detail));
    }
}
