package com.project.socialX.repository;

import com.project.socialX.domain.ReelLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReelLikeRepository extends JpaRepository<ReelLike, Long> {
    Optional<ReelLike> findByReelIdAndUserId(Long reelId, Long userId);
    boolean existsByReelIdAndUserId(Long reelId, Long userId);
    long countByReelId(Long reelId);
}
