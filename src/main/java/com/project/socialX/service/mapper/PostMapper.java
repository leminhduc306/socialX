package com.project.socialX.service.mapper;

import com.project.socialX.domain.Post;
import com.project.socialX.domain.PostMedia;
import com.project.socialX.domain.User;
import com.project.socialX.service.dto.Post.PostMediaResponse;
import com.project.socialX.service.dto.Post.PostResponse;
import com.project.socialX.service.dto.Post.PostUserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = DefaultConfigMapper.class)
public interface PostMapper {

    @Mapping(target = "avatarUrl", source = "userDetails.avatarUrl")
    PostUserSummaryResponse userToUserSummaryResponse(User user);

    PostMediaResponse postMediaToPostMediaResponse(PostMedia postMedia);

    List<PostMediaResponse> postMediaListToPostMediaResponseList(List<PostMedia> postMediaList);

    PostResponse toResponse(Post entity);
    
    List<PostResponse> toResponseList(List<Post> entities);
}
