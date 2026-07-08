package com.zerotrust.auth.repository;

import com.zerotrust.auth.entity.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {
    List<DeviceSession> findByUserId(Long userId);
}
