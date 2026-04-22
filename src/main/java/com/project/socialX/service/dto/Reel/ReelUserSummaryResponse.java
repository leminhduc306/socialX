package com.project.socialX.service.dto.Reel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReelUserSummaryResponse {
    Long id;
    String username;
    String avatarUrl;
}
