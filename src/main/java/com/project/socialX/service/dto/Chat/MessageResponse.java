package com.project.socialX.service.dto.Chat;

import com.project.socialX.domain.enums.MessageType;
import com.project.socialX.service.dto.Post.PostUserSummaryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    Long id;
    String content;
    MessageType type;
    PostUserSummaryResponse sender;
    Long conversationId;
    Instant createdDate;
}
