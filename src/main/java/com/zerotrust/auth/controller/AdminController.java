package com.zerotrust.auth.controller;

import com.zerotrust.auth.dto.response.MessageResponse;
import com.zerotrust.auth.dto.response.UserProfileResponse;
import com.zerotrust.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('READ_ALL_USERS')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('READ_ALL_USERS')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<MessageResponse> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        if (role == null || role.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Role is required"));
        }
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(new MessageResponse("User role updated successfully"));
    }

    @PatchMapping("/users/{id}/lock")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<MessageResponse> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok(new MessageResponse("User account locked successfully"));
    }

    @PatchMapping("/users/{id}/unlock")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<MessageResponse> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(new MessageResponse("User account unlocked successfully"));
    }
}
