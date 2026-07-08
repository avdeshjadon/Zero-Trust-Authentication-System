package com.zerotrust.auth.service;

import com.zerotrust.auth.dto.request.ChangePasswordRequest;
import com.zerotrust.auth.dto.request.UpdateProfileRequest;
import com.zerotrust.auth.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    
    // Admin features
    List<UserProfileResponse> getAllUsers();
    UserProfileResponse getUserById(Long userId);
    void updateUserRole(Long userId, String roleName);
    void lockUser(Long userId);
    void unlockUser(Long userId);
}
