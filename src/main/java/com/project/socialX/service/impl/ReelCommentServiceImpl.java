package com.project.socialX.service.impl;

import com.project.socialX.domain.Reel;
import com.project.socialX.domain.ReelComment;
import com.project.socialX.domain.User;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.repository.ReelCommentRepository;
import com.project.socialX.repository.ReelRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.ReelCommentService;
import com.project.socialX.service.dto.ReelComment.ReelCommentRequest;
import com.project.socialX.service.dto.ReelComment.ReelCommentResponse;
import com.project.socialX.service.mapper.ReelCommentMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReelCommentServiceImpl implements ReelCommentService {

    private final ReelCommentRepository reelCommentRepository;
    private final ReelRepository reelRepository;
    private final UserRepository userRepository;
    private final ReelCommentMapper reelCommentMapper;

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    @Transactional
    public ReelCommentResponse createComment(Long reelId, ReelCommentRequest request) {
        User user = currentUser();
        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new BadRequestException("Reel not found with id: " + reelId));

        ReelComment reelComment = ReelComment.builder()
                .content(request.getContent())
                .user(user)
                .reel(reel)
                .build();

        if (request.getParentId() != null) {
            ReelComment parent = reelCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BadRequestException("Parent comment not found with id: " + request.getParentId()));
            reelComment.setParentComment(parent);
        }

        ReelComment saved = reelCommentRepository.save(reelComment);
        return reelCommentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReelCommentResponse updateComment(Long commentId, ReelCommentRequest request) {
        User user = currentUser();
        ReelComment reelComment = reelCommentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Comment not found with id: " + commentId));

        if (!reelComment.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to update this comment");
        }

        reelComment.setContent(request.getContent());
        ReelComment saved = reelCommentRepository.save(reelComment);
        return reelCommentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        User user = currentUser();
        ReelComment reelComment = reelCommentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Comment not found with id: " + commentId));

        if (!reelComment.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to delete this comment");
        }

        reelCommentRepository.delete(reelComment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<ReelCommentResponse> getCommentsByReel(Long reelId, PagingRequest pagingRequest) {
        Page<ReelComment> commentPage = reelCommentRepository.findByReelIdAndParentCommentIsNull(reelId, pagingRequest.pageable());
        Page<ReelCommentResponse> responsePage = commentPage.map(reelCommentMapper::toResponse);
        return PagingResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReelCommentResponse> getReplies(Long commentId) {
        List<ReelComment> replies = reelCommentRepository.findByParentCommentId(commentId);
        return replies.stream().map(reelCommentMapper::toResponse).collect(Collectors.toList());
    }
}
