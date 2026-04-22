package com.project.socialX.service.mapper;

import com.project.socialX.domain.PostComment;
import com.project.socialX.domain.User;
import com.project.socialX.service.dto.Comment.CommentResponse;
import com.project.socialX.service.dto.Post.PostUserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = DefaultConfigMapper.class)
public interface CommentMapper {

    @Mapping(target = "parentId", source = "parentComment.id")
    @Mapping(target = "user", source = "user")
    CommentResponse toResponse(PostComment postComment);

    @Mapping(target = "avatarUrl", source = "userDetails.avatarUrl")
    PostUserSummaryResponse userToUserSummaryResponse(User user);
}
