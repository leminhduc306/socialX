package com.project.socialX.service;

import com.project.socialX.service.dto.User.request.ChangePasswordRequest;

public interface UserService {
    void changePassword(ChangePasswordRequest request);
}
