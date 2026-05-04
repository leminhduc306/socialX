package com.project.socialX.service.mapper;

import com.project.socialX.domain.Conversation;
import com.project.socialX.domain.Message;
import com.project.socialX.domain.User;
import com.project.socialX.service.dto.Chat.ConversationResponse;
import com.project.socialX.service.dto.Chat.MessageResponse;
import com.project.socialX.service.dto.Post.PostUserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = DefaultConfigMapper.class)
public interface ChatMapper {

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "sender", source = "sender")
    MessageResponse toMessageResponse(Message message);

    @Mapping(target = "lastMessage", source = "lastMessage")
    @Mapping(target = "participants", ignore = true)
    ConversationResponse toConversationResponse(Conversation conversation);

    @Mapping(target = "avatarUrl", source = "userDetails.avatarUrl")
    PostUserSummaryResponse userToUserSummaryResponse(User user);

    List<PostUserSummaryResponse> usersToUserSummaryResponses(List<User> users);
}
