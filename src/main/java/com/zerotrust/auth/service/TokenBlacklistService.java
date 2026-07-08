package com.zerotrust.auth.service;

public interface TokenBlacklistService {
    void blacklistToken(String token, long expirationInMs);
    boolean isTokenBlacklisted(String token);
}
