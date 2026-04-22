package com.project.socialX.service.dto.User;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowStatusResponse {
    private boolean following;
    private long followerCount;
    private long followingCount;
}
