package com.project.socialX.service.dto.Chat;

import com.project.socialX.domain.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    String content;
    MessageType type;
    Long conversationId;
    Long recipientId; // Dùng cho 1-1 chat khi chưa có conversationId
}
