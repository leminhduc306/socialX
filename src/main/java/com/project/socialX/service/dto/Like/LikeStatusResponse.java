package com.project.socialX.service.dto.Like;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeStatusResponse {
    private boolean liked;
    private long likeCount;
}
