package com.project.socialX.service.impl;

import com.project.socialX.domain.Conversation;
import com.project.socialX.domain.ConversationParticipant;
import com.project.socialX.domain.User;
import com.project.socialX.domain.enums.ConversationType;
import com.project.socialX.domain.enums.ParticipantRole;
import com.project.socialX.repository.ConversationParticipantRepository;
import com.project.socialX.repository.ConversationRepository;
import com.project.socialX.repository.UserRepository;
import com.project.socialX.security.SecurityUtils;
import com.project.socialX.service.ConversationService;
import com.project.socialX.service.dto.Chat.ConversationResponse;
import com.project.socialX.service.dto.Chat.CreateGroupRequest;
import com.project.socialX.service.mapper.ChatMapper;
import com.project.socialX.web.rest.errors.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    private User currentUser() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new BadRequestException("Unauthenticated"));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations() {
        User user = currentUser();
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(user.getId());

        return conversations.stream().map(c -> {
            ConversationResponse res = chatMapper.toConversationResponse(c);
            List<ConversationParticipant> participants = participantRepository.findByConversationId(c.getId());
            List<User> participantUsers = participants.stream()
                    .map(ConversationParticipant::getUser)
                    .collect(Collectors.toList());
            res.setParticipants(chatMapper.usersToUserSummaryResponses(participantUsers));

            if (c.getType() == ConversationType.DIRECT) {
                participantUsers.stream()
                        .filter(u -> !u.getId().equals(user.getId()))
                        .findFirst()
                        .ifPresent(otherUser -> {
                            res.setName(otherUser.getUsername());
                            if (otherUser.getUserDetails() != null) {
                                res.setAvatarUrl(otherUser.getUserDetails().getAvatarUrl());
                            }
                        });
            }

            return res;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationResponse getOrCreateDirectConversation(Long targetUserId) {
        User user = currentUser();
        if (user.getId().equals(targetUserId)) {
            throw new BadRequestException("You cannot create a conversation with yourself");
        }
        Conversation conversation = conversationRepository.findDirectConversation(user.getId(), targetUserId)
                .orElseGet(() -> createDirectConversationEntity(user, targetUserId));

        return buildConversationResponse(conversation, user);
    }

    @Override
    @Transactional
    public ConversationResponse createGroupConversation(CreateGroupRequest request) {
        if (request.getParticipantIds().size() < 2) {
            throw new BadRequestException("Một nhóm chat phải có ít nhất 3 người");
        }
        User owner = currentUser();

        Conversation conversation = Conversation.builder()
                .name(request.getName())
                .type(ConversationType.GROUP)
                .build();
        conversation = conversationRepository.save(conversation);

        List<ConversationParticipant> participants = new ArrayList<>();
        participants.add(ConversationParticipant.builder()
                .conversation(conversation).user(owner).role(ParticipantRole.ADMIN).build());

        for (Long id : request.getParticipantIds()) {
            if (id.equals(owner.getId())) continue;
            User user = userRepository.findById(id).orElse(null);
            if (user != null) {
                participants.add(ConversationParticipant.builder()
                        .conversation(conversation).user(user).role(ParticipantRole.MEMBER).build());
            }
        }
        participantRepository.saveAll(participants);

        ConversationResponse res = chatMapper.toConversationResponse(conversation);
        res.setParticipants(chatMapper.usersToUserSummaryResponses(
                participants.stream().map(ConversationParticipant::getUser).collect(Collectors.toList())));
        return res;
    }

    @Override
    @Transactional
    public ConversationResponse addMember(Long conversationId, Long userId) {
        User currentUser = currentUser();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BadRequestException("Conversation not found"));

        if (conversation.getType() != ConversationType.GROUP) {
            throw new BadRequestException("Cannot add members to a direct conversation");
        }

        // Chỉ ADMIN mới được thêm người
        ConversationParticipant currentParticipant = participantRepository
                .findByConversationIdAndUserId(conversationId, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("You are not a member of this conversation"));
        if (currentParticipant.getRole() != ParticipantRole.ADMIN) {
            throw new BadRequestException("Only admin can add members");
        }

        // Kiểm tra user đã là member chưa
        if (participantRepository.findByConversationIdAndUserId(conversationId, userId).isPresent()) {
            throw new BadRequestException("User is already a member");
        }

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        participantRepository.save(ConversationParticipant.builder()
                .conversation(conversation).user(newMember).role(ParticipantRole.MEMBER).build());

        return buildConversationResponse(conversation, currentUser);
    }

    @Override
    @Transactional
    public void removeMember(Long conversationId, Long userId) {
        User currentUser = currentUser();
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BadRequestException("Conversation not found"));

        if (conversation.getType() != ConversationType.GROUP) {
            throw new BadRequestException("Cannot remove members from a direct conversation");
        }

        ConversationParticipant currentParticipant = participantRepository
                .findByConversationIdAndUserId(conversationId, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("You are not a member of this conversation"));

        // ADMIN có thể kick bất kỳ ai, MEMBER chỉ có thể rời nhóm (tự remove chính mình)
        if (currentParticipant.getRole() != ParticipantRole.ADMIN && !currentUser.getId().equals(userId)) {
            throw new BadRequestException("Only admin can remove other members");
        }

        ConversationParticipant target = participantRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BadRequestException("User is not a member of this conversation"));

        participantRepository.delete(target);
    }

    // === Helper methods ===

    private Conversation createDirectConversationEntity(User sender, Long recipientId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new BadRequestException("Recipient not found"));

        Conversation conversation = Conversation.builder().type(ConversationType.DIRECT).build();
        conversation = conversationRepository.save(conversation);

        participantRepository.save(ConversationParticipant.builder()
                .conversation(conversation).user(sender).role(ParticipantRole.MEMBER).build());
        participantRepository.save(ConversationParticipant.builder()
                .conversation(conversation).user(recipient).role(ParticipantRole.MEMBER).build());

        return conversation;
    }

    private ConversationResponse buildConversationResponse(Conversation conversation, User currentUser) {
        ConversationResponse res = chatMapper.toConversationResponse(conversation);
        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversation.getId());
        List<User> participantUsers = participants.stream()
                .map(ConversationParticipant::getUser).collect(Collectors.toList());
        res.setParticipants(chatMapper.usersToUserSummaryResponses(participantUsers));

        if (conversation.getType() == ConversationType.DIRECT) {
            participantUsers.stream()
                    .filter(u -> !u.getId().equals(currentUser.getId()))
                    .findFirst()
                    .ifPresent(otherUser -> {
                        res.setName(otherUser.getUsername());
                        if (otherUser.getUserDetails() != null) {
                            res.setAvatarUrl(otherUser.getUserDetails().getAvatarUrl());
                        }
                    });
        }
        return res;
    }
}
