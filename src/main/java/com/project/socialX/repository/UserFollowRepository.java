package com.project.socialX.repository;

import com.project.socialX.domain.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    long countByFollowerId(Long followerId); // Đang follow bao nhiêu người
    long countByFollowingId(Long followingId); // Có bao nhiêu người follow mình
}
