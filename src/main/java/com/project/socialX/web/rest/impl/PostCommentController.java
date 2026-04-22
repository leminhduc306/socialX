package com.project.socialX.web.rest.impl;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.dto.response.Response;
import com.project.socialX.service.CommentService;
import com.project.socialX.service.dto.Comment.CommentRequest;
import com.project.socialX.service.dto.Comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostCommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Response<CommentResponse>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request) {
        CommentResponse result = commentService.createComment(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.created(result));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Response<PagingResponse<CommentResponse>>> getCommentsByPost(
            @PathVariable Long postId,
            @ModelAttribute PagingRequest pagingRequest) {
        return ResponseEntity.ok(Response.ok(commentService.getCommentsByPost(postId, pagingRequest)));
    }

    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<Response<List<CommentResponse>>> getReplies(@PathVariable Long commentId) {
        return ResponseEntity.ok(Response.ok(commentService.getReplies(commentId)));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(Response.ok(commentService.updateComment(commentId, request)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Response<Void>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Response.ok(null));
    }
}
