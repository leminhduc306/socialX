package com.project.socialX.service;

import com.project.socialX.service.dto.Chat.ConversationResponse;
import com.project.socialX.service.dto.Chat.CreateGroupRequest;

import java.util.List;

public interface ConversationService {
    List<ConversationResponse> getMyConversations();
    ConversationResponse getOrCreateDirectConversation(Long targetUserId);
    ConversationResponse createGroupConversation(CreateGroupRequest request);
    ConversationResponse addMember(Long conversationId, Long userId);
    void removeMember(Long conversationId, Long userId);
    ConversationResponse updateGroupInfo(Long conversationId, String name, org.springframework.web.multipart.MultipartFile avatar);
}
