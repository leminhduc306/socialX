package com.project.socialX.service;

import com.project.socialX.service.dto.Like.LikeStatusResponse;

public interface LikeService {
    LikeStatusResponse togglePostLike(Long postId);
    LikeStatusResponse toggleReelLike(Long reelId);
}
