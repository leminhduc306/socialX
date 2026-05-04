package com.project.socialX.service.dto.Chat;

import com.project.socialX.domain.enums.ConversationType;
import com.project.socialX.service.dto.Post.PostUserSummaryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    Long id;
    String name;
    ConversationType type;
    String avatarUrl;
    MessageResponse lastMessage;
    List<PostUserSummaryResponse> participants;
}
