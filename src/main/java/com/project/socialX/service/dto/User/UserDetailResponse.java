package com.project.socialX.service.dto.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.socialX.domain.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * DTO trả về cho client sau khi xử lý UserDetail.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponse {

    Long id;
    String phoneNumber;
    String address;
    String bio;
    String avatarUrl;
    Gender gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;

    long followerCount;
    long followingCount;
    boolean isFollowing;
}
