package com.project.socialX.repository;

import com.project.socialX.domain.SearchHistory;
import com.project.socialX.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Page<SearchHistory> findByOwnerIdOrderBySearchTimeDesc(Long ownerId, Pageable pageable);
    
    Optional<SearchHistory> findByOwnerAndTarget(User owner, User target);
    
    void deleteByOwnerId(Long ownerId);
}
