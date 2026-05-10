package com.project.socialX.service;

import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.dto.User.UserDetailRequest;
import com.project.socialX.service.dto.User.UserDetailResponse;
import com.project.socialX.service.dto.User.UserFilterRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserDetailService {

    // ── My Profile (lấy từ Security Context) ──────────────────────────────────

    UserDetailResponse getMyUserDetail();

    UserDetailResponse createMyUserDetail(UserDetailRequest request, MultipartFile avatar);

    UserDetailResponse updateMyUserDetail(UserDetailRequest request, MultipartFile avatar);

    UserDetailResponse getUserDetailByUsername(String username);

    // ── Search ────────────────────────────────────────────────────────────────

    PagingResponse<UserDetailResponse> searchUsers(UserFilterRequest filter);

    PagingResponse<UserDetailResponse> getSearchHistory();

    void saveSearchHistory(Long targetId);

    void deleteSearchHistory(Long targetId);

    void clearSearchHistory();

    // ── Admin (truyền userId tùy ý) ───────────────────────────────────────────

    UserDetailResponse getUserDetail(Long userId);

    UserDetailResponse createUserDetail(Long userId, UserDetailRequest request, MultipartFile avatar);

    UserDetailResponse updateUserDetail(Long userId, UserDetailRequest request, MultipartFile avatar);
}
