package com.project.socialX.service.dto.Post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostUserSummaryResponse {
    Long id;
    String username;
    String avatarUrl;
}
