package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.response.Response;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.UserDetailService;
import com.project.socialX.service.dto.User.UserDetailRequest;
import com.project.socialX.service.dto.User.UserDetailResponse;
import com.project.socialX.service.dto.User.UserFilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserDetailController {

    private final UserDetailService userDetailService;

    // ─── My Profile ───────────────────────────────────────────────────────────

    @PostMapping("/api/users/search")
    public ResponseEntity<Response<PagingResponse<UserDetailResponse>>> searchUsers(
            @RequestBody UserFilterRequest filter) {
        return ResponseEntity.ok(Response.ok(userDetailService.searchUsers(filter)));
    }

    @GetMapping("/api/users/search-history")
    public ResponseEntity<Response<PagingResponse<UserDetailResponse>>> getSearchHistory() {
        return ResponseEntity.ok(Response.ok(userDetailService.getSearchHistory()));
    }

    @PostMapping("/api/users/search-history/{targetId}")
    public ResponseEntity<Response<Void>> saveSearchHistory(@PathVariable Long targetId) {
        userDetailService.saveSearchHistory(targetId);
        return ResponseEntity.ok(Response.ok(null));
    }

    @DeleteMapping("/api/users/search-history/{targetId}")
    public ResponseEntity<Response<Void>> deleteSearchHistory(@PathVariable Long targetId) {
        userDetailService.deleteSearchHistory(targetId);
        return ResponseEntity.ok(Response.ok(null));
    }

    @DeleteMapping("/api/users/search-history")
    public ResponseEntity<Response<Void>> clearSearchHistory() {
        userDetailService.clearSearchHistory();
        return ResponseEntity.ok(Response.ok(null));
    }

    @GetMapping("/api/users/profile/{username}")
    public ResponseEntity<Response<UserDetailResponse>> getUserDetailByUsername(@PathVariable String username) {
        return ResponseEntity.ok(Response.ok(userDetailService.getUserDetailByUsername(username)));
    }

    @GetMapping("/api/user-details")
    public ResponseEntity<Response<UserDetailResponse>> getMyUserDetail() {
        return ResponseEntity.ok(Response.ok(userDetailService.getMyUserDetail()));
    }

    @PostMapping(value = "/api/user-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UserDetailResponse>> createMyUserDetail(
            @RequestPart("request") UserDetailRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        UserDetailResponse result = userDetailService.createMyUserDetail(request, avatar);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    @PutMapping(value = "/api/user-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UserDetailResponse>> updateMyUserDetail(
            @RequestPart("request") UserDetailRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ResponseEntity.ok(Response.ok(userDetailService.updateMyUserDetail(request, avatar)));
    }

    // ─── Admin ────────────────────────────────────────────────────────────────

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/user-details/{userId}")
    public ResponseEntity<Response<UserDetailResponse>> getUserDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(Response.ok(userDetailService.getUserDetail(userId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/user-details/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UserDetailResponse>> createUserDetail(
            @PathVariable Long userId,
            @RequestPart("request") UserDetailRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        UserDetailResponse result = userDetailService.createUserDetail(userId, request, avatar);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/api/admin/user-details/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<UserDetailResponse>> updateUserDetail(
            @PathVariable Long userId,
            @RequestPart("request") UserDetailRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ResponseEntity.ok(Response.ok(userDetailService.updateUserDetail(userId, request, avatar)));
    }
}
