package com.gtm.gtm.notification.dto;

import com.gtm.gtm.notification.domain.NotificationType;

import java.time.OffsetDateTime;
import java.util.Map;

public record NotificationDto(
        Long id,
        NotificationType type,
        String title,
        String message,
        Map<String, Object> payload,
        boolean read,
        OffsetDateTime createdAt
) {}
