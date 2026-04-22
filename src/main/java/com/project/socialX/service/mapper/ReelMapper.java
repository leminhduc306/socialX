package com.project.socialX.service.mapper;


import com.project.socialX.domain.Reel;
import com.project.socialX.domain.User;
import com.project.socialX.service.dto.Reel.ReelResponse;
import com.project.socialX.service.dto.Reel.ReelUserSummaryResponse;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface ReelMapper {

    @Mapping(target = "avatarUrl", source = "userDetails.avatarUrl")
    ReelUserSummaryResponse userToReelUserSummaryResponse(User user);

    ReelResponse toResponse(Reel reel);
}
