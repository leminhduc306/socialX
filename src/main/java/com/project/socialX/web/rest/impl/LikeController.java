package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.response.Response;
import com.project.socialX.service.LikeService;
import com.project.socialX.service.dto.Like.LikeStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Response<LikeStatusResponse>> togglePostLike(@PathVariable Long postId) {
        return ResponseEntity.ok(Response.ok(likeService.togglePostLike(postId)));
    }

    @PostMapping("/reels/{reelId}/like")
    public ResponseEntity<Response<LikeStatusResponse>> toggleReelLike(@PathVariable Long reelId) {
        return ResponseEntity.ok(Response.ok(likeService.toggleReelLike(reelId)));
    }
}
