package com.project.socialX.service.impl;

import com.project.socialX.domain.*;
import com.project.socialX.repository.*;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.LikeService;
import com.project.socialX.service.dto.Like.LikeStatusResponse;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final ReelLikeRepository reelLikeRepository;
    private final PostRepository postRepository;
    private final ReelRepository reelRepository;
    private final UserRepository userRepository;

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    @Transactional
    public LikeStatusResponse togglePostLike(Long postId) {
        User user = currentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found"));

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId());
        boolean liked;
        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            liked = false;
        } else {
            PostLike newLike = PostLike.builder().post(post).user(user).build();
            postLikeRepository.save(newLike);
            liked = true;
        }

        long count = postLikeRepository.countByPostId(postId);
        return LikeStatusResponse.builder().liked(liked).likeCount(count).build();
    }

    @Override
    @Transactional
    public LikeStatusResponse toggleReelLike(Long reelId) {
        User user = currentUser();
        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new BadRequestException("Reel not found"));

        Optional<ReelLike> existingLike = reelLikeRepository.findByReelIdAndUserId(reelId, user.getId());
        boolean liked;
        if (existingLike.isPresent()) {
            reelLikeRepository.delete(existingLike.get());
            liked = false;
        } else {
            ReelLike newLike = ReelLike.builder().reel(reel).user(user).build();
            reelLikeRepository.save(newLike);
            liked = true;
        }

        long count = reelLikeRepository.countByReelId(reelId);
        return LikeStatusResponse.builder().liked(liked).likeCount(count).build();
    }
}
