package com.zerotrust.auth.service.impl;

import com.zerotrust.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${token.blacklist-prefix}")
    private String blacklistPrefix;

    @Override
    public void blacklistToken(String token, long expirationInMs) {
        String key = blacklistPrefix + token;
        redisTemplate.opsForValue().set(key, "revoked", expirationInMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = blacklistPrefix + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
