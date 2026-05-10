package com.project.socialX.service.impl;

import com.project.socialX.domain.User;
import com.project.socialX.domain.UserDetail;
import com.project.socialX.intergration.MinioChannel;
import com.project.socialX.repository.UserDetailRepository;
import com.project.socialX.repository.UserFollowRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.repository.PostRepository;
import com.project.socialX.repository.SearchHistoryRepository;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.UserDetailService;
import com.project.socialX.service.dto.User.UserDetailRequest;
import com.project.socialX.service.dto.User.UserDetailResponse;
import com.project.socialX.service.dto.User.UserFilterRequest;
import com.project.socialX.service.mapper.UserDetailMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    private final UserDetailRepository userDetailRepository;
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserDetailMapper userDetailMapper;
    private final PostRepository postRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final MinioChannel minioChannel;

    // ─── helpers ──────────────────────────────────────────────────────────────

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

    /**
     * Upload avatar lên MinIO nếu file hợp lệ, trả về public URL. Trả null nếu
     * không có file.
     */
    private String uploadAvatar(MultipartFile avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            return minioChannel.upload(avatar);
        }
        return null;
    }

    private void setUserDetailResponseStats(UserDetailResponse response, Long targetUserId) {
        if (response == null)
            return;

        response.setFollowerCount(userFollowRepository.countByFollowingId(targetUserId));
        response.setFollowingCount(userFollowRepository.countByFollowerId(targetUserId));
        response.setPostCount(postRepository.countByUserId(targetUserId));

        String currentEmail = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (currentEmail != null) {
            userRepository.findByEmail(currentEmail).ifPresent(user -> {
                response.setFollowing(
                        userFollowRepository.existsByFollowerIdAndFollowingId(user.getId(), targetUserId));
            });
        } else {
            response.setFollowing(false);
        }
    }

    // ─── My Profile ───────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getMyUserDetail() {
        User user = currentUser();
        UserDetail detail = userDetailRepository.findById(user.getId())
                .orElseThrow(() -> new BadRequestException("User detail not found"));
        UserDetailResponse response = userDetailMapper.toResponse(detail);
        setUserDetailResponseStats(response, user.getId());
        return response;
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
        if (avatarUrl != null)
            detail.setAvatarUrl(avatarUrl);

        UserDetail saved = userDetailRepository.save(detail);
        UserDetailResponse response = userDetailMapper.toResponse(saved);
        setUserDetailResponseStats(response, userId);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetailByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found with username: " + username));
        UserDetail detail = userDetailRepository.findById(user.getId())
                .orElseThrow(() -> new BadRequestException("User detail not found"));
        UserDetailResponse response = userDetailMapper.toResponse(detail);
        setUserDetailResponseStats(response, user.getId());
        return response;
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
        if (avatarUrl != null)
            detail.setAvatarUrl(avatarUrl);

        UserDetail saved = userDetailRepository.save(detail);
        UserDetailResponse response = userDetailMapper.toResponse(saved);
        setUserDetailResponseStats(response, userId);
        return response;
    }

    // ─── Admin ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        resolveUser(userId);
        UserDetail detail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User detail not found for userId: " + userId));
        UserDetailResponse response = userDetailMapper.toResponse(detail);
        setUserDetailResponseStats(response, userId);
        return response;
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
        if (avatarUrl != null)
            detail.setAvatarUrl(avatarUrl);

        UserDetail saved = userDetailRepository.save(detail);
        UserDetailResponse response = userDetailMapper.toResponse(saved);
        setUserDetailResponseStats(response, userId);
        return response;
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserDetail(Long userId, UserDetailRequest request, MultipartFile avatar) {
        resolveUser(userId);

        UserDetail detail = userDetailRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User detail not found for userId: " + userId));

        userDetailMapper.partialUpdate(request, detail);

        String avatarUrl = uploadAvatar(avatar);
        if (avatarUrl != null)
            detail.setAvatarUrl(avatarUrl);

        UserDetail saved = userDetailRepository.save(detail);
        UserDetailResponse response = userDetailMapper.toResponse(saved);
        setUserDetailResponseStats(response, userId);
        return response;
    }

    // ─── Search & History ─────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<UserDetailResponse> searchUsers(UserFilterRequest filter) {
        if (filter == null) {
            return PagingResponse.from(Page.empty());
        }

        User current = currentUser();
        filter.setExcludedId(current.getId());

        final Page<UserDetailResponse> users = userRepository
                .findAll(filter.specification(), filter.getPaging().pageable())
                .map(user -> {
                    if (user.getUserDetails() == null)
                        return null;
                    UserDetailResponse res = userDetailMapper.toResponse(user.getUserDetails());
                    setUserDetailResponseStats(res, user.getId());
                    return res;
                });
        return PagingResponse.from(users);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<UserDetailResponse> getSearchHistory() {
        User user = currentUser();
        final Page<UserDetailResponse> history = searchHistoryRepository
                .findByOwnerIdOrderBySearchTimeDesc(user.getId(), PageRequest.of(0, 15))
                .map(item -> {
                    User target = item.getTarget();
                    if (target == null || target.getUserDetails() == null)
                        return null;
                    UserDetailResponse res = userDetailMapper.toResponse(target.getUserDetails());
                    setUserDetailResponseStats(res, target.getId());
                    return res;
                });
        return PagingResponse.from(history);
    }

    @Override
    @Transactional
    public void saveSearchHistory(Long targetId) {
        User owner = currentUser();
        User target = resolveUser(targetId);
        if (owner.getId().equals(targetId))
            return;

        searchHistoryRepository.findByOwnerAndTarget(owner, target)
                .ifPresentOrElse(
                        history -> history.setSearchTime(java.time.LocalDateTime.now()),
                        () -> searchHistoryRepository.save(com.project.socialX.domain.SearchHistory.builder()
                                .owner(owner)
                                .target(target)
                                .searchTime(java.time.LocalDateTime.now())
                                .build()));
    }

    @Override
    @Transactional
    public void deleteSearchHistory(Long targetId) {
        User owner = currentUser();
        User target = resolveUser(targetId);
        searchHistoryRepository.findByOwnerAndTarget(owner, target)
                .ifPresent(searchHistoryRepository::delete);
    }

    @Override
    @Transactional
    public void clearSearchHistory() {
        User user = currentUser();
        searchHistoryRepository.deleteByOwnerId(user.getId());
    }
}
