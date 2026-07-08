package com.zerotrust.auth.service;

import com.zerotrust.auth.entity.RefreshToken;

public interface TokenService {
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUserId(Long userId);
    RefreshToken rotateRefreshToken(String requestRefreshToken);
    void revokeToken(String tokenHash, Long userId);
}
