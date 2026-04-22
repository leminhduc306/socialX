package com.project.socialX.service.dto.Reel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReelResponse {
    Long id;
    String caption;
    ReelUserSummaryResponse user;
    String videoUrl;
    Instant createdDate;
    Instant lastModifiedDate;
    Long likeCount;
    Long commentCount;
    Boolean isLiked;
}
