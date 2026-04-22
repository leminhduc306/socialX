package com.project.socialX.service.mapper;

import com.project.socialX.domain.ReelComment;
import com.project.socialX.domain.User;
import com.project.socialX.service.dto.Post.PostUserSummaryResponse;
import com.project.socialX.service.dto.ReelComment.ReelCommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = DefaultConfigMapper.class)
public interface ReelCommentMapper {

    @Mapping(target = "parentId", source = "parentComment.id")
    @Mapping(target = "user", source = "user")
    ReelCommentResponse toResponse(ReelComment reelComment);

    @Mapping(target = "avatarUrl", source = "userDetails.avatarUrl")
    PostUserSummaryResponse userToUserSummaryResponse(User user);
}
