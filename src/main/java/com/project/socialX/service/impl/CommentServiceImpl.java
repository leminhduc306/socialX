package com.project.socialX.service.impl;

import com.project.socialX.domain.Post;
import com.project.socialX.domain.PostComment;
import com.project.socialX.domain.User;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.repository.PostCommentRepository;
import com.project.socialX.repository.PostRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.CommentService;
import com.project.socialX.service.dto.Comment.CommentRequest;
import com.project.socialX.service.dto.Comment.CommentResponse;
import com.project.socialX.service.mapper.CommentMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request) {
        User user = currentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("Post not found with id: " + postId));

        PostComment postComment = PostComment.builder()
                .content(request.getContent())
                .user(user)
                .post(post)
                .build();

        if (request.getParentId() != null) {
            PostComment parent = postCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BadRequestException("Parent comment not found with id: " + request.getParentId()));
            postComment.setParentComment(parent);
        }

        PostComment saved = postCommentRepository.save(postComment);

        // Cập nhật số lượng comment của bài viết
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        User user = currentUser();
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Comment not found with id: " + commentId));

        if (!postComment.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to update this comment");
        }

        postComment.setContent(request.getContent());
        PostComment saved = postCommentRepository.save(postComment);
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        User user = currentUser();
        PostComment postComment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("Comment not found with id: " + commentId));

        // Chủ comment hoặc chủ bài viết (hoặc admin) mới được xóa?
        // Ở đây tạm để chủ comment xóa
        if (!postComment.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bạn không được phép xóa bình luận này");
        }

        Post post = postComment.getPost();
        postCommentRepository.delete(postComment);
        
        // Giảm số lượng comment
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<CommentResponse> getCommentsByPost(Long postId, PagingRequest pagingRequest) {
        Page<PostComment> commentPage = postCommentRepository.findByPostIdAndParentCommentIsNull(postId, pagingRequest.pageable());
        Page<CommentResponse> responsePage = commentPage.map(comment -> {
            CommentResponse res = commentMapper.toResponse(comment);
            res.setReplyCount((long) postCommentRepository.findByParentCommentId(comment.getId()).size());
            return res;
        });
        return PagingResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(Long commentId) {
        List<PostComment> replies = postCommentRepository.findByParentCommentId(commentId);
        return replies.stream().map(reply -> {
            CommentResponse res = commentMapper.toResponse(reply);
            res.setReplyCount((long) postCommentRepository.findByParentCommentId(reply.getId()).size());
            return res;
        }).collect(Collectors.toList());
    }
}
