package com.feellog.backend.domain.notification.repository;

import com.feellog.backend.domain.notification.entity.DeviceToken;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByToken(String token);

    List<DeviceToken> findByUser(User user);

    void deleteByUserAndToken(User user, String token);
}