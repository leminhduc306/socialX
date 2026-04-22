package com.project.socialX.service;

import com.project.socialX.service.dto.User.FollowStatusResponse;

public interface UserFollowService {
    FollowStatusResponse toggleFollow(Long targetUserId);
}
