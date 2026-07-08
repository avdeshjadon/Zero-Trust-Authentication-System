package com.zerotrust.auth.service.impl;

import com.zerotrust.auth.dto.request.LoginRequest;
import com.zerotrust.auth.dto.request.RegisterRequest;
import com.zerotrust.auth.dto.request.TokenRefreshRequest;
import com.zerotrust.auth.dto.response.JwtAuthResponse;
import com.zerotrust.auth.entity.RefreshToken;
import com.zerotrust.auth.entity.Role;
import com.zerotrust.auth.entity.User;
import com.zerotrust.auth.exception.BadRequestException;
import com.zerotrust.auth.exception.ResourceNotFoundException;
import com.zerotrust.auth.repository.RoleRepository;
import com.zerotrust.auth.repository.UserRepository;
import com.zerotrust.auth.security.CustomUserDetails;
import com.zerotrust.auth.security.jwt.JwtProvider;
import com.zerotrust.auth.service.AuditService;
import com.zerotrust.auth.service.AuthService;
import com.zerotrust.auth.service.TokenBlacklistService;
import com.zerotrust.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditService auditService;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Error: Email is already in use!");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);
        auditService.logAction("REGISTER", null, registerRequest.getEmail(), null, "User registered successfully");
    }

    @Override
    public JwtAuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtProvider.generateAccessToken(userDetails.getUsername());
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = tokenService.createRefreshToken(userDetails.getId());
        
        auditService.logAction("LOGIN_SUCCESS", userDetails.getId(), userDetails.getUsername(), request, "User logged in successfully");

        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getTokenHash()) // This is the raw token string temporarily
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .roles(roles)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public JwtAuthResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken newRefreshToken = tokenService.rotateRefreshToken(requestRefreshToken);
        
        User user = newRefreshToken.getUser();
        String jwt = jwtProvider.generateAccessToken(user.getEmail());
        
        List<String> roles = user.getRole().getPermissions().stream()
                .map(p -> p.getName())
                .collect(Collectors.toList());
        roles.add(user.getRole().getName());
        
        auditService.logAction("REFRESH_TOKEN", user.getId(), user.getEmail(), null, "Tokens refreshed");

        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(newRefreshToken.getTokenHash())
                .id(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void logout(String accessToken, Long userId) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            long expirationTime = jwtProvider.getExpirationFromJwtToken(accessToken) - System.currentTimeMillis();
            if (expirationTime > 0) {
                tokenBlacklistService.blacklistToken(accessToken, expirationTime);
            }
        }
        auditService.logAction("LOGOUT", userId, null, null, "User logged out");
    }

    @Override
    @Transactional
    public void logoutAll(Long userId) {
        tokenService.deleteByUserId(userId);
        auditService.logAction("LOGOUT_ALL", userId, null, null, "User logged out from all devices");
    }
}
