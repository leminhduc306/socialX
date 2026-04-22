package com.project.socialX.service.dto.ReelComment;

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
public class ReelCommentResponse {
    Long id;
    String content;
    PostUserSummaryResponse user;
    Long parentId;
    Instant createdDate;
    Instant lastModifiedDate;
}
