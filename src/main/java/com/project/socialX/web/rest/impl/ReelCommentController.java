package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.dto.response.Response;
import com.project.socialX.service.ReelCommentService;
import com.project.socialX.service.dto.ReelComment.ReelCommentRequest;
import com.project.socialX.service.dto.ReelComment.ReelCommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReelCommentController {

    private final ReelCommentService reelCommentService;

    @PostMapping("/reels/{reelId}/comments")
    public ResponseEntity<Response<ReelCommentResponse>> createComment(
            @PathVariable Long reelId,
            @RequestBody ReelCommentRequest request) {
        ReelCommentResponse result = reelCommentService.createComment(reelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    @GetMapping("/reels/{reelId}/comments")
    public ResponseEntity<Response<PagingResponse<ReelCommentResponse>>> getCommentsByReel(
            @PathVariable Long reelId,
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(reelCommentService.getCommentsByReel(reelId, pagingRequest)));
    }

    @GetMapping("/reel-comments/{commentId}/replies")
    public ResponseEntity<Response<List<ReelCommentResponse>>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(Response.ok(reelCommentService.getReplies(commentId)));
    }

    @PutMapping("/reel-comments/{commentId}")
    public ResponseEntity<Response<ReelCommentResponse>> updateComment(
            @PathVariable Long commentId,
            @RequestBody ReelCommentRequest request) {
        return ResponseEntity.ok(Response.ok(reelCommentService.updateComment(commentId, request)));
    }

    @DeleteMapping("/reel-comments/{commentId}")
    public ResponseEntity<Response<Void>> deleteComment(@PathVariable Long commentId) {
        reelCommentService.deleteComment(commentId);
        return ResponseEntity.ok(Response.ok(null));
    }
}
