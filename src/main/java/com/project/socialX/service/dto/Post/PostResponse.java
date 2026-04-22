package com.project.socialX.service.dto.Post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
    Long id;
    String caption;
    PostUserSummaryResponse user;
    List<PostMediaResponse> mediaList;
    Instant createdDate;
    Instant lastModifiedDate;
    Long likeCount;
    Long commentCount;
    Boolean isLiked;
}
