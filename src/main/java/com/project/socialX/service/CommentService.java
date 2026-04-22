package com.project.socialX.service;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.dto.Comment.CommentRequest;
import com.project.socialX.service.dto.Comment.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(Long postId, CommentRequest request);

    CommentResponse updateComment(Long commentId, CommentRequest request);

    void deleteComment(Long commentId);

    PagingResponse<CommentResponse> getCommentsByPost(Long postId, PagingRequest pagingRequest);

    List<CommentResponse> getReplies(Long commentId);
}
