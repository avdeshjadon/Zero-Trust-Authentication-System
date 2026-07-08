package com.zerotrust.auth.service.impl;

import com.zerotrust.auth.entity.RefreshToken;
import com.zerotrust.auth.entity.RevokedToken;
import com.zerotrust.auth.entity.User;
import com.zerotrust.auth.exception.ResourceNotFoundException;
import com.zerotrust.auth.exception.TokenRefreshException;
import com.zerotrust.auth.repository.RefreshTokenRepository;
import com.zerotrust.auth.repository.RevokedTokenRepository;
import com.zerotrust.auth.repository.UserRepository;
import com.zerotrust.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.refresh-token-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String tokenStr = UUID.randomUUID().toString();
        String tokenHash = hashToken(tokenStr);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(LocalDateTime.now().plusNanos(refreshTokenDurationMs * 1_000_000))
                .tokenHash(tokenHash)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        
        RefreshToken returnToken = new RefreshToken();
        returnToken.setId(refreshToken.getId());
        returnToken.setUser(refreshToken.getUser());
        returnToken.setExpiryDate(refreshToken.getExpiryDate());
        returnToken.setTokenHash(tokenStr); // return raw token

        return returnToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getTokenHash(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public RefreshToken rotateRefreshToken(String requestRefreshToken) {
        String tokenHash = hashToken(requestRefreshToken);
        
        // Check if token is revoked
        if (revokedTokenRepository.existsByTokenHash(tokenHash)) {
            throw new TokenRefreshException(requestRefreshToken, "Token is revoked and cannot be used");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));

        verifyExpiration(refreshToken);

        Long userId = refreshToken.getUser().getId();
        
        // Invalidate old token
        refreshTokenRepository.delete(refreshToken);
        
        // Add to revoked
        revokeToken(tokenHash, userId);

        // Create new token
        return createRefreshToken(userId);
    }

    @Override
    @Transactional
    public void revokeToken(String tokenHash, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        RevokedToken revokedToken = RevokedToken.builder()
                .tokenHash(tokenHash)
                .user(user)
                .build();
        revokedTokenRepository.save(revokedToken);
    }
    
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
