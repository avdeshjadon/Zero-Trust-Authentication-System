package com.zerotrust.auth.controller;

import com.zerotrust.auth.dto.request.ChangePasswordRequest;
import com.zerotrust.auth.dto.request.UpdateProfileRequest;
import com.zerotrust.auth.dto.response.MessageResponse;
import com.zerotrust.auth.dto.response.UserProfileResponse;
import com.zerotrust.auth.security.CustomUserDetails;
import com.zerotrust.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('READ_PROFILE')")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse profile = userService.getUserProfile(userDetails.getId());
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    @PreAuthorize("hasAuthority('UPDATE_PROFILE')")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(userDetails.getId(), request);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAuthority('UPDATE_PROFILE')")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }
}
