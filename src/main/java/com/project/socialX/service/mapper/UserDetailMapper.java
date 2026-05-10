package com.project.socialX.service.mapper;

import com.project.socialX.domain.UserDetail;
import com.project.socialX.service.dto.User.UserDetailRequest;
import com.project.socialX.service.dto.User.UserDetailResponse;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface UserDetailMapper {

    UserDetail toEntity(UserDetailRequest request);

    @Mapping(source = "user.username", target = "username")
    UserDetailResponse toResponse(UserDetail entity);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
    )
    void partialUpdate(UserDetailRequest request, @MappingTarget UserDetail entity);
}
