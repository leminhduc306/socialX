package com.project.socialX.service.dto.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.socialX.domain.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * DTO nhận request từ client khi tạo hoặc cập nhật UserDetail.
 * Không chứa avatarUrl — avatar được gửi riêng dưới dạng MultipartFile.
 * Tất cả trường đều optional: trường nào null thì khi update sẽ bị bỏ qua (PATCH semantics).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailRequest {

    String phoneNumber;
    String address;
    String bio;
    Gender gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
}
