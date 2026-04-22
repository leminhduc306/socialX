package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.response.Response;
import com.project.socialX.service.UserFollowService;
import com.project.socialX.service.dto.User.FollowStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserFollowController {

    private final UserFollowService userFollowService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Response<FollowStatusResponse>> toggleFollow(@PathVariable Long userId) {
        return ResponseEntity.ok(Response.ok(userFollowService.toggleFollow(userId)));
    }
}
