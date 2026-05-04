package com.project.socialX.service;

import com.project.socialX.domain.enums.MessageType;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.service.dto.Chat.MessageRequest;
import com.project.socialX.service.dto.Chat.MessageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ChatService {
    MessageResponse sendMessage(MessageRequest request);
    MessageResponse sendMediaMessage(Long conversationId, Long recipientId, MultipartFile file, MessageType type);
    PagingResponse<MessageResponse> getMessages(Long conversationId, PagingRequest pagingRequest);
}
