package com.project.socialX.service.impl;

import com.project.socialX.domain.Conversation;
import com.project.socialX.domain.ConversationParticipant;
import com.project.socialX.domain.Message;
import com.project.socialX.domain.User;
import com.project.socialX.domain.enums.ConversationType;
import com.project.socialX.domain.enums.MessageType;
import com.project.socialX.dto.page.PagingRequest;
import com.project.socialX.dto.page.PagingResponse;
import com.project.socialX.intergration.MinioChannel;
import com.project.socialX.repository.ConversationParticipantRepository;
import com.project.socialX.repository.ConversationRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.repository.MessageRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.ChatService;
import com.project.socialX.service.dto.Chat.MessageRequest;
import com.project.socialX.service.dto.Chat.MessageResponse;
import com.project.socialX.service.mapper.ChatMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final MinioChannel minioChannel;

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private void validateParticipant(Long conversationId, Long userId) {
        if (!conversationRepository.isParticipant(conversationId, userId)) {
            throw new BadRequestException("You are not a member of this conversation");
        }
    }

    private Conversation resolveConversation(User sender, Long conversationId, Long recipientId) {
        if (conversationId != null) {
            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new BadRequestException("Conversation not found"));
            validateParticipant(conversation.getId(), sender.getId());
            return conversation;
        } else if (recipientId != null) {
            if (sender.getId().equals(recipientId)) {
                throw new BadRequestException("You cannot send a message to yourself");
            }
            return conversationRepository.findDirectConversation(sender.getId(), recipientId)
                    .orElseThrow(() -> new BadRequestException("Conversation not found, create one first"));
        } else {
            throw new BadRequestException("Conversation ID or Recipient ID is required");
        }
    }

    private MessageResponse saveAndBroadcast(Conversation conversation, User sender, String content, MessageType type) {
        Message message = Message.builder()
                .content(content).type(type).sender(sender).conversation(conversation).build();
        message = messageRepository.save(message);

        conversation.setLastMessage(message);
        conversationRepository.save(conversation);

        MessageResponse response = chatMapper.toMessageResponse(message);

        if (conversation.getType() == ConversationType.GROUP) {
            messagingTemplate.convertAndSend("/topic/conversation." + conversation.getId(), response);
        } else {
            List<ConversationParticipant> participants = participantRepository.findByConversationId(conversation.getId());
            for (ConversationParticipant p : participants) {
                messagingTemplate.convertAndSendToUser(p.getUser().getEmail(), "/queue/messages", response);
            }
        }
        return response;
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(MessageRequest request) {
        User sender = currentUser();
        Conversation conversation = resolveConversation(sender, request.getConversationId(), request.getRecipientId());
        return saveAndBroadcast(conversation, sender, request.getContent(), request.getType());
    }

    @Override
    @Transactional
    public MessageResponse sendMediaMessage(Long conversationId, Long recipientId, MultipartFile file, MessageType type) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        User sender = currentUser();
        Conversation conversation = resolveConversation(sender, conversationId, recipientId);
        String mediaUrl = minioChannel.upload(file);
        return saveAndBroadcast(conversation, sender, mediaUrl, type);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse<MessageResponse> getMessages(Long conversationId, PagingRequest pagingRequest) {
        User user = currentUser();
        validateParticipant(conversationId, user.getId());
        Page<Message> messagePage = messageRepository.findByConversationIdOrderByCreatedDateDesc(conversationId, pagingRequest.pageable());
        return PagingResponse.from(messagePage.map(chatMapper::toMessageResponse));
    }
}
