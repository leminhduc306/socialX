package com.project.socialX.service;

import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.dto.ReelComment.ReelCommentRequest;
import com.project.socialX.service.dto.ReelComment.ReelCommentResponse;

import java.util.List;

public interface ReelCommentService {
    ReelCommentResponse createComment(Long reelId, ReelCommentRequest request);

    ReelCommentResponse updateComment(Long commentId, ReelCommentRequest request);

    void deleteComment(Long commentId);

    PagingResponse<ReelCommentResponse> getCommentsByReel(Long reelId, PagingRequest pagingRequest);

    List<ReelCommentResponse> getReplies(Long commentId);
}
