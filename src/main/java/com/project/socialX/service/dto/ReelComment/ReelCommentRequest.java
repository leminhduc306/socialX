package com.project.socialX.service.dto.ReelComment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReelCommentRequest {
    String content;
    Long parentId;
}
