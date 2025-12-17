package com.gtm.gtm.notification.repository;

import com.gtm.gtm.common.repository.SoftDeleteRepository;
import com.gtm.gtm.notification.domain.Notification;
import com.gtm.gtm.notification.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends SoftDeleteRepository<Notification, Long> {

    Page<Notification> findAllByUser_Id(Long userId, Pageable pageable);

    Page<Notification> findAllByUser_IdAndRead(Long userId, boolean read, Pageable pageable);

    Page<Notification> findAllByUser_IdAndType(Long userId, NotificationType type, Pageable pageable);

    Page<Notification> findAllByUser_IdAndTypeAndRead(Long userId, NotificationType type, boolean read, Pageable pageable);

    Optional<Notification> findByIdAndUser_Id(Long id, Long userId);

    @Modifying
    @Query("update Notification n set n.read = true, n.updatedAt = CURRENT_TIMESTAMP where n.user.id = :userId and n.read = false")
    void markAllReadForUser(@Param("userId") Long userId);
}
