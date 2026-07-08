package com.zerotrust.auth.service.impl;

import com.zerotrust.auth.entity.AuditLog;
import com.zerotrust.auth.repository.AuditLogRepository;
import com.zerotrust.auth.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(String action, Long userId, String username, HttpServletRequest request, String details) {
        String ipAddress = null;
        String userAgent = null;

        if (request != null) {
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            userAgent = request.getHeader("User-Agent");
        }

        AuditLog log = AuditLog.builder()
                .action(action)
                .userId(userId)
                .username(username)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .details(details)
                .build();

        auditLogRepository.save(log);
    }
}
