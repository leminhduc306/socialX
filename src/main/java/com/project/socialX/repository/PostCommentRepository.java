package com.project.socialX.repository;

import com.project.socialX.domain.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    
    // Lấy các bình luận gốc (không có cha) của một bài viết
    Page<PostComment> findByPostIdAndParentCommentIsNull(Long postId, Pageable pageable);

    List<PostComment> findByParentCommentId(Long parentId);
    long countByPostId(Long postId);
}
