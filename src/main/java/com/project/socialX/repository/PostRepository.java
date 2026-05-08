package com.project.socialX.repository;

import com.project.socialX.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN (SELECT f.following.id FROM UserFollow f WHERE f.follower.id = :userId) OR p.user.id = :userId ORDER BY p.createdDate DESC")
    Page<Post> findFeed(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (p.user.id IN (SELECT f.following.id FROM UserFollow f WHERE f.follower.id = :userId) OR p.user.id = :userId) " +
           "AND (:lastId IS NULL OR p.id < :lastId) ORDER BY p.id DESC")
    List<Post> findFeedCursor(@Param("userId") Long userId, @Param("lastId") Long lastId, Pageable pageable);
}
