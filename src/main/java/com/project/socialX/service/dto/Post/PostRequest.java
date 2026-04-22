package com.project.socialX.service.dto.Post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    String caption;
    List<Long> deletedMediaIds;
}
