package com.feellog.backend.domain.notification.repository;

import com.feellog.backend.domain.notification.entity.Notification;
import com.feellog.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user);

    Optional<Notification> findByNotificationIdAndUserAndIsDeletedFalse(Long notificationId, User user);

    @Modifying
    @Query("update Notification n " +
            "set n.isRead = true, n.readAt = CURRENT_TIMESTAMP " +
            "where n.user = :user and n.isRead = false and n.isDeleted = false")
    int markAllAsReadByUser(@Param("user") User user);

    @Modifying
    @Query("update Notification n " +
            "set n.isDeleted = true, n.deletedAt = CURRENT_TIMESTAMP " +
            "where n.user = :user and n.isDeleted = false")
    int softDeleteAllByUser(@Param("user") User user);
}