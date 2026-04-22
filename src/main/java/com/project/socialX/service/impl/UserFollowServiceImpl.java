package com.project.socialX.service.impl;

import com.project.socialX.domain.User;
import com.project.socialX.domain.UserFollow;
import com.project.socialX.repository.UserFollowRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.UserFollowService;
import com.project.socialX.service.dto.User.FollowStatusResponse;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    @Transactional
    public FollowStatusResponse toggleFollow(Long targetUserId) {
        User follower = currentUser();
        User following = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BadRequestException("User to follow not found"));

        if (follower.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        Optional<UserFollow> existingFollow = userFollowRepository.findByFollowerIdAndFollowingId(follower.getId(), targetUserId);
        boolean isFollowing;

        if (existingFollow.isPresent()) {
            userFollowRepository.delete(existingFollow.get());
            isFollowing = false;
        } else {
            UserFollow newFollow = UserFollow.builder()
                    .follower(follower)
                    .following(following)
                    .build();
            userFollowRepository.save(newFollow);
            isFollowing = true;
        }

        return FollowStatusResponse.builder()
                .following(isFollowing)
                .followerCount(userFollowRepository.countByFollowingId(targetUserId))
                .followingCount(userFollowRepository.countByFollowerId(targetUserId))
                .build();
    }
}
