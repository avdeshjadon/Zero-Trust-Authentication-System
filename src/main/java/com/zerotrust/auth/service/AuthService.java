package com.zerotrust.auth.service;

import com.zerotrust.auth.dto.request.LoginRequest;
import com.zerotrust.auth.dto.request.RegisterRequest;
import com.zerotrust.auth.dto.request.TokenRefreshRequest;
import com.zerotrust.auth.dto.response.JwtAuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    JwtAuthResponse login(LoginRequest loginRequest, HttpServletRequest request);
    JwtAuthResponse refreshToken(TokenRefreshRequest request);
    void logout(String accessToken, Long userId);
    void logoutAll(Long userId);
}
