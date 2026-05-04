package com.project.socialX.repository;

import com.project.socialX.domain.Conversation;
import com.project.socialX.domain.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT cp.conversation FROM ConversationParticipant cp " +
           "WHERE cp.user.id = :userId " +
           "ORDER BY cp.conversation.lastModifiedDate DESC")
    List<Conversation> findConversationsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c " +
           "JOIN ConversationParticipant cp1 ON c.id = cp1.conversation.id " +
           "JOIN ConversationParticipant cp2 ON c.id = cp2.conversation.id " +
           "WHERE c.type = 'DIRECT' AND cp1.user.id = :userId1 AND cp2.user.id = :userId2")
    Optional<Conversation> findDirectConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END " +
           "FROM ConversationParticipant cp " +
           "WHERE cp.conversation.id = :conversationId AND cp.user.id = :userId")
    boolean isParticipant(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}
