package com.project.socialX.repository;

import com.project.socialX.domain.ReelComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelCommentRepository extends JpaRepository<ReelComment, Long> {
    
    // Lấy các bình luận gốc của một Reel
    Page<ReelComment> findByReelIdAndParentCommentIsNull(Long reelId, Pageable pageable);

    // Lấy các phản hồi của một bình luận Reel
    List<ReelComment> findByParentCommentId(Long parentId);
}
