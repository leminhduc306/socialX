package com.project.socialX.repository;

import com.project.socialX.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Tìm các bài viết của một người dùng nhất định, có phân trang
    Page<Post> findByUserId(Long userId, Pageable pageable);
}
