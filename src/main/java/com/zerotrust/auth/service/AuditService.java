package com.zerotrust.auth.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AuditService {
    void logAction(String action, Long userId, String username, HttpServletRequest request, String details);
}
