package com.zerotrust.auth.controller;

import com.zerotrust.auth.dto.request.LoginRequest;
import com.zerotrust.auth.dto.request.RegisterRequest;
import com.zerotrust.auth.dto.request.TokenRefreshRequest;
import com.zerotrust.auth.dto.response.JwtAuthResponse;
import com.zerotrust.auth.dto.response.MessageResponse;
import com.zerotrust.auth.security.CustomUserDetails;
import com.zerotrust.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        JwtAuthResponse response = authService.login(loginRequest, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        JwtAuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
            
        authService.logout(accessToken, userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Log out successful"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logoutAllDevices(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logoutAll(userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Logged out from all devices successfully"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<CustomUserDetails> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Return minimal details or mapped DTO
        return ResponseEntity.ok(userDetails);
    }
}
