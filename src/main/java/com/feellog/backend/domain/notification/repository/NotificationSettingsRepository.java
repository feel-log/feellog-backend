package com.feellog.backend.domain.notification.repository;

import com.feellog.backend.domain.notification.entity.NotificationSettings;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    Optional<NotificationSettings> findByUser(User user);
}